package spring.restapi.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spring.restapi.model.*;
import spring.restapi.repository.*;
import spring.restapi.response_dto.*;
import spring.restapi.utils.Constants;
import spring.restapi.utils.Global;
import spring.restapi.utils.RedisPublisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DoorService {

    private final UserListRepository userListRepository;
    private final UserRoleListRepository userRoleListRepository;
    private final DistrictRepository districtRepository;
    private final DeviceListRepository deviceListRepository;
    private final BusinessListRepository businessListRepository;
    private final DeviceInfoHistoryRepository deviceInfoHistoryRepository;
    private final DevicePropertyRepository devicePropertyRepository;
    private final DeviceOperateHistoryRepository deviceOperateHistoryRepository;
    private final MessageListRepository messageListRepository;

    public DoorService(UserListRepository userListRepository, UserRoleListRepository userRoleListRepository, DistrictRepository districtRepository, DeviceListRepository deviceListRepository, BusinessListRepository businessListRepository, DeviceInfoHistoryRepository deviceInfoHistoryRepository, DevicePropertyRepository devicePropertyRepository, DeviceOperateHistoryRepository deviceOperateHistoryRepository, MessageListRepository messageListRepository) {
        this.userListRepository = userListRepository;
        this.userRoleListRepository = userRoleListRepository;
        this.districtRepository = districtRepository;
        this.deviceListRepository = deviceListRepository;
        this.businessListRepository = businessListRepository;
        this.deviceInfoHistoryRepository = deviceInfoHistoryRepository;
        this.devicePropertyRepository = devicePropertyRepository;
        this.deviceOperateHistoryRepository = deviceOperateHistoryRepository;
        this.messageListRepository = messageListRepository;
    }

    public BaseResponseDto login(String cid, String password) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Search in "user_list"
        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }

        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.DISABLE));
            return baseResponseDto;
        }
        if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }
        long userId = user.getId();

        // Search in "user_role_list"
        Optional<UserRoleList> opUserRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL);
        if (!opUserRole.isPresent()) {
            opUserRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_REQUEST);
            if (opUserRole.isPresent()) {
                baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
                baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            } else {
                baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
                baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            }
            return baseResponseDto;
        }
        UserRoleList userRole = opUserRole.get();
        String roleType = userRole.getRoleType();
        long deviceId = userRole.getDeviceId();

        // Search in "device_list"
        Optional<DeviceList> opDevice = this.deviceListRepository.findById(deviceId);
        if (!opDevice.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_LINKED_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_LINKED_DEVICE));
            return baseResponseDto;
        }
        DeviceList device = opDevice.get();
        if (device.getState() != Constants.STATE_NORMAL) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INVALID_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INVALID_DEVICE));
            return baseResponseDto;
        }
        int districtId = device.getDistrictId();

        // Search in "district"
        District district = this.districtRepository.findById(districtId).orElse(null);
        if (district == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_DISTRICT));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_DISTRICT));
            return baseResponseDto;
        }

        // Mapping result
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.SUCCESS));
        loginResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.SUCCESS));
        loginResponseDto.setCid(cid);
        loginResponseDto.setName(user.getName());
        loginResponseDto.setGender(user.getGender());
        loginResponseDto.setBirthday(user.getBirthday());
        loginResponseDto.setCitizenNumber(user.getCitizenNumber());
        loginResponseDto.setInstallPlace(device.getInstallPlace());
        loginResponseDto.setRoleType(roleType);
        loginResponseDto.setState(Constants.STATE_NORMAL);
        loginResponseDto.setDistrictId(districtId);
        loginResponseDto.setDistrictInfo(device.getDistrictInfo());
        loginResponseDto.setDetailInfo(device.getDetailInfo());
        loginResponseDto.setDeviceId(deviceId);

        if (device.getInstallPlace().equals(Constants.INSTALL_PLACE_HOME)) {
            String[] detailSplits = device.getDetailInfo().split(":");
            loginResponseDto.setConvDetailInfo(device.getDistrictInfo() + " " + detailSplits[0] + " unit " + detailSplits[1] + " floor " + detailSplits[2] + " index");
        } else {
            loginResponseDto.setConvDetailInfo(device.getDetailInfo());
        }

        if (roleType.equals(Constants.ROLE_TYPE_ADMIN)) {
            List<DeviceProperty> propertyList = this.devicePropertyRepository.findAllByDeviceId(deviceId);
            JsonObject propertyObj = new JsonObject();
            for (DeviceProperty deviceProperty : propertyList) {
                if (deviceProperty.getDeviceProperty().equals(Constants.DEVICE_PROPERTY_ENCRYPTION_KEY))
                    continue;
                propertyObj.addProperty(deviceProperty.getDeviceProperty(), deviceProperty.getPropertyValue());
            }

            loginResponseDto.setPropertyInfo(propertyObj.toString());
        }

        return loginResponseDto;
    }

    public BaseResponseDto checkRegisterUser(String cid) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Search in "user_list"
        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getCheckRegisterCode(Constants.CHECK_REGISTER_CODES.SUCCESS));
            baseResponseDto.setRspMsg(Constants.getCheckRegisterMessage(Constants.CHECK_REGISTER_CODES.SUCCESS));
            return baseResponseDto;
        }
        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getCheckRegisterCode(Constants.CHECK_REGISTER_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getCheckRegisterMessage(Constants.CHECK_REGISTER_CODES.DISABLE));
            return baseResponseDto;
        } else if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getCheckRegisterCode(Constants.CHECK_REGISTER_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getCheckRegisterMessage(Constants.CHECK_REGISTER_CODES.REQUEST));
            return baseResponseDto;
        } else {
            baseResponseDto.setRspCode(Constants.getCheckRegisterCode(Constants.CHECK_REGISTER_CODES.NORMAL));
            baseResponseDto.setRspMsg(Constants.getCheckRegisterMessage(Constants.CHECK_REGISTER_CODES.NORMAL));
            return baseResponseDto;
        }
    }

    public BaseResponseDto registerUser(String cid, String name, String password, String gender, String birthday, String citizenNumber, String installPlace, int districtId, String unit, String floor, String index, long businessId, String authType) {
        BaseResponseDto baseResponseDto = checkRegisterUser(cid);
        if (!baseResponseDto.getRspCode().equals(Constants.getCheckRegisterCode(Constants.CHECK_REGISTER_CODES.SUCCESS))) {
            return baseResponseDto;
        }

        // Extract Address Detail Info
        String businessInfo = "";
        String homeInfo = "";
        if (installPlace.equals(Constants.INSTALL_PLACE_HOME)) {
            homeInfo = unit + ":" + floor + ":" + index;
        }
        if (installPlace.equals(Constants.INSTALL_PLACE_BUSINESS) && businessId > 0) {
            BusinessList business = this.businessListRepository.findById(businessId).orElse(null);
            if (business != null) {
                businessInfo = business.getName();
            } else {
                baseResponseDto.setRspCode(Constants.getRegisterCode(Constants.REGISTER_CODES.NO_BUSINESS));
                baseResponseDto.setRspMsg(Constants.getRegisterMessage(Constants.REGISTER_CODES.NO_BUSINESS));
                return baseResponseDto;
            }
        }
        String detailInfo = installPlace.equals(Constants.INSTALL_PLACE_HOME) ? homeInfo : businessInfo;

        // Check device using districtInfo and address info
        DeviceList device = this.deviceListRepository.findFirstByInstallPlaceAndDistrictIdAndDetailInfoAndState(installPlace, districtId, detailInfo, Constants.STATE_NORMAL).orElse(null);
        if (device == null) {
            baseResponseDto.setRspCode(Constants.getRegisterCode(Constants.REGISTER_CODES.NO_DEVICE));
            baseResponseDto.setRspMsg(Constants.getRegisterMessage(Constants.REGISTER_CODES.NO_DEVICE));
            return baseResponseDto;
        }

        // Add User
        UserList newUser = new UserList();
        newUser.setCid(Long.parseLong(cid));
        newUser.setName(name);
        newUser.setPassword(password);
        newUser.setGender(gender);
        newUser.setBirthday(birthday);
        newUser.setCitizenNumber(citizenNumber);
        newUser.setState(Constants.STATE_NORMAL);
        newUser = this.userListRepository.save(newUser);

        // Add User-Role
        UserRoleList newUserRole = new UserRoleList();
        newUserRole.setUserId(newUser.getId());
        newUserRole.setDeviceId(device.getId());
        newUserRole.setRoleType(authType);
        newUserRole.setState(Constants.STATE_REQUEST);
        this.userRoleListRepository.save(newUserRole);


        baseResponseDto.setRspCode(Constants.getRegisterCode(Constants.REGISTER_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getRegisterMessage(Constants.REGISTER_CODES.SUCCESS));
        return baseResponseDto;
    }

    public BaseResponseDto searchDistrict(int districtId) {
        if (districtId < 0) districtId = 0;

        District district = this.districtRepository.findById(districtId).orElse(null);
        if (district == null && districtId != 0) {
            BaseResponseDto baseResponseDto = new BaseResponseDto();
            baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.NO_DATA));
            baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.NO_DATA));
            return baseResponseDto;
        }

        List<District> listDistrict = this.districtRepository.findAllByParentIdAndStateOrderByDispOrder(districtId, Constants.STATE_NORMAL);

        SearchDistrictResponseDto responseDto = new SearchDistrictResponseDto();
        responseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        responseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        responseDto.setId(districtId);
        responseDto.setName(district == null ? "" : district.getName());
        responseDto.setParentId(district == null ? 0 : district.getParentId());
        responseDto.setChildDistricts(listDistrict);

        return responseDto;
    }

    public BaseResponseDto getBusinessList() {
        List<BusinessList> listBusiness = this.businessListRepository.findAllByStateOrderByDispOrderAsc(Constants.STATE_NORMAL);
        BusinessListResponseDto responseDto = new BusinessListResponseDto();
        responseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        responseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        responseDto.setBusinessList(listBusiness);
        return responseDto;
    }

    public BaseResponseDto changeUserinfo(String cid, String name, String password, String gender, String birthday, String citizenNumber) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }

        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.DISABLE));
            return baseResponseDto;
        }
        if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }

        user.setName(name);
        user.setGender(gender);
        user.setBirthday(birthday);
        user.setCitizenNumber(citizenNumber);
        this.userListRepository.save(user);

        baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        return baseResponseDto;
    }

    public BaseResponseDto changePassword(String cid, String curPassword, String newPassword) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }

        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.DISABLE));
            return baseResponseDto;
        }
        if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(curPassword)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }

        user.setPassword(newPassword);
        this.userListRepository.save(user);

        baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        return baseResponseDto;
    }

    public BaseResponseDto requestAdminAuth(String cid, String password, long deviceId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Check User
        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }

        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.DISABLE));
            return baseResponseDto;
        }
        if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }

        // Check User Role with deviceId
        UserRoleList userRole = this.userRoleListRepository.findFirstByUserIdAndDeviceIdAndRoleTypeAndStateNot(user.getId(), deviceId, Constants.ROLE_TYPE_ADMIN, Constants.STATE_DELETE).orElse(null);
        if (userRole != null) {
            switch (userRole.getState()) {
                case Constants.STATE_DISABLE:
                    baseResponseDto.setRspCode(Constants.getRequestAdminAuthCode(Constants.REQUEST_ADMIN_AUTH_CODES.DISABLE));
                    baseResponseDto.setRspMsg(Constants.getRequestAdminAuthMessage(Constants.REQUEST_ADMIN_AUTH_CODES.DISABLE));
                    return baseResponseDto;
                case Constants.STATE_REQUEST:
                    baseResponseDto.setRspCode(Constants.getRequestAdminAuthCode(Constants.REQUEST_ADMIN_AUTH_CODES.REQUEST));
                    baseResponseDto.setRspMsg(Constants.getRequestAdminAuthMessage(Constants.REQUEST_ADMIN_AUTH_CODES.REQUEST));
                    return baseResponseDto;
                case Constants.STATE_NORMAL:
                    baseResponseDto.setRspCode(Constants.getRequestAdminAuthCode(Constants.REQUEST_ADMIN_AUTH_CODES.NORMAL));
                    baseResponseDto.setRspMsg(Constants.getRequestAdminAuthMessage(Constants.REQUEST_ADMIN_AUTH_CODES.NORMAL));
                    return baseResponseDto;
            }
        }

        UserRoleList newRole = new UserRoleList();
        newRole.setUserId(user.getId());
        newRole.setDeviceId(deviceId);
        newRole.setRoleType(Constants.ROLE_TYPE_ADMIN);
        newRole.setState(Constants.STATE_REQUEST);
        this.userRoleListRepository.save(newRole);

        baseResponseDto.setRspCode(Constants.getRequestAdminAuthCode(Constants.REQUEST_ADMIN_AUTH_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getRequestAdminAuthMessage(Constants.REQUEST_ADMIN_AUTH_CODES.SUCCESS));
        return baseResponseDto;
    }

    public BaseResponseDto checkDoorStates(String cid, String password, long deviceId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Check User
        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }

        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.DISABLE));
            return baseResponseDto;
        }
        if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }

        // Insert Network Socket Connection State
        DeviceInfoHistory socketInfoHistory = this.deviceInfoHistoryRepository.findFirstByDeviceIdAndType(deviceId, Constants.DEVICE_INFO_TYPE_SOCKET).orElse(null);
        if (socketInfoHistory == null) {
            socketInfoHistory = new DeviceInfoHistory();
            socketInfoHistory.setDeviceId(deviceId);
            socketInfoHistory.setType(Constants.DEVICE_INFO_TYPE_SOCKET);
            socketInfoHistory.setValue("0");
            socketInfoHistory = this.deviceInfoHistoryRepository.save(socketInfoHistory);
        }

        // Insert Bluetooth Connection state
        DeviceInfoHistory bluetoothInfoHistory = this.deviceInfoHistoryRepository.findFirstByDeviceIdAndType(deviceId, Constants.DEVICE_INFO_TYPE_BLUETOOTH).orElse(null);
        if (bluetoothInfoHistory == null) {
            bluetoothInfoHistory = new DeviceInfoHistory();
            bluetoothInfoHistory.setDeviceId(deviceId);
            bluetoothInfoHistory.setType(Constants.DEVICE_INFO_TYPE_BLUETOOTH);
            bluetoothInfoHistory.setValue("0");
            bluetoothInfoHistory = this.deviceInfoHistoryRepository.save(socketInfoHistory);
        }

        // Insert Battery State
        DeviceInfoHistory batteryInfoHistory = this.deviceInfoHistoryRepository.findFirstByDeviceIdAndType(deviceId, Constants.DEVICE_INFO_TYPE_BATTERY).orElse(null);
        if (batteryInfoHistory == null) {
            batteryInfoHistory = new DeviceInfoHistory();
            batteryInfoHistory.setDeviceId(deviceId);
            batteryInfoHistory.setType(Constants.DEVICE_INFO_TYPE_BATTERY);
            batteryInfoHistory.setValue("0");
            batteryInfoHistory = this.deviceInfoHistoryRepository.save(socketInfoHistory);
        }

        long currentTime = System.currentTimeMillis();
        int socketConnected = Math.abs(currentTime - Long.parseLong(socketInfoHistory.getValue())) <= Global.socketTimeout ? 1 : 0;
        int doorConnected = Math.abs(currentTime - Long.parseLong(bluetoothInfoHistory.getValue())) <= Global.bluetoothTimeout ? 1 : 0;
        int batteryLevel = Math.abs(currentTime - Long.parseLong(bluetoothInfoHistory.getValue())) <= Global.bluetoothTimeout ? Integer.parseInt(batteryInfoHistory.getValue()) : -1;

        CheckStatesResponseDto statesResponseDto = new CheckStatesResponseDto();
        statesResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        statesResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        statesResponseDto.setSocketConnected(socketConnected);
        statesResponseDto.setDoorConnected(doorConnected);
        statesResponseDto.setBatteryLevel(batteryLevel);

        return statesResponseDto;
    }

    public BaseResponseDto openDoorRequest(String cid, String password) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Check User
        Optional<UserList> opUser = this.userListRepository.findFirstByCidAndStateNot(Long.parseLong(cid), Constants.STATE_DELETE);
        if (!opUser.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }

        UserList user = opUser.get();
        if (user.getState() == Constants.STATE_DISABLE) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.DISABLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.DISABLE));
            return baseResponseDto;
        }
        if (user.getState() == Constants.STATE_REQUEST) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }

        long userId = user.getId();

        // Check User Role
        Optional<UserRoleList> opUserRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL);
        if (!opUserRole.isPresent()) {
            opUserRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_REQUEST);
            if (opUserRole.isPresent()) {
                baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.REQUEST));
                baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.REQUEST));
            } else {
                baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
                baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            }
            return baseResponseDto;
        }
        UserRoleList userRole = opUserRole.get();
        long deviceId = userRole.getDeviceId();

        // Search in "device_list"
        Optional<DeviceList> opDevice = this.deviceListRepository.findById(deviceId);
        if (!opDevice.isPresent()) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_LINKED_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_LINKED_DEVICE));
            return baseResponseDto;
        }
        DeviceList device = opDevice.get();
        if (device.getState() != Constants.STATE_NORMAL) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INVALID_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INVALID_DEVICE));
            return baseResponseDto;
        }

        // Encrypt Request Data
        DeviceProperty property = this.devicePropertyRepository.findFirstByDeviceIdAndDeviceProperty(deviceId, Constants.DEVICE_PROPERTY_ENCRYPTION_KEY).orElse(null);
        if (property == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INVALID_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INVALID_DEVICE));
            return baseResponseDto;
        }
        String encryptionKey = property.getPropertyValue();

        property = this.devicePropertyRepository.findFirstByDeviceIdAndDeviceProperty(deviceId, Constants.DEVICE_PROPERTY_BLUETOOTH_ADDRESS).orElse(null);
        if (property == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INVALID_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INVALID_DEVICE));
            return baseResponseDto;
        }
        String bluetoothAddress = property.getPropertyValue();

        String encryptData = Global.AESEncrypt(bluetoothAddress + ":" + userId + ":" + System.currentTimeMillis(), encryptionKey, Constants.IV_PARAM);

        // Find Admin Role User
        UserRoleList adminUserRole = this.userRoleListRepository.findFirstByDeviceIdAndRoleTypeAndState(deviceId, Constants.ROLE_TYPE_ADMIN, Constants.STATE_NORMAL).orElse(null);
        if (adminUserRole == null) {
            baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.NO_CONNECTED_ADMIN));
            baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.NO_CONNECTED_ADMIN));
            return baseResponseDto;
        }

        UserList adminUser = this.userListRepository.findById(adminUserRole.getUserId()).orElse(null);
        if (adminUser == null) {
            baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.NO_CONNECTED_ADMIN));
            baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.NO_CONNECTED_ADMIN));
            return baseResponseDto;
        }

        if (adminUser.getState() != Constants.STATE_NORMAL) {
            baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.NO_CONNECTED_ADMIN));
            baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.NO_CONNECTED_ADMIN));
            return baseResponseDto;
        }

        // Insert Door Operate History
        DeviceOperateHistory newHistory = new DeviceOperateHistory();
        newHistory.setUserId(userId);
        newHistory.setDeviceId(deviceId);
        newHistory.setLinkId(adminUser.getId());
        newHistory.setAction(Constants.DEVICE_OPERATE_TYPE_REQUEST);
        newHistory.setMode(Constants.DEVICE_OPERATE_MODE_PHONE);
        newHistory.setActionDateInt(Global.getDateIntValue());
        this.deviceOperateHistoryRepository.save(newHistory);

        // Publish Redis queue
        JsonObject socketObj = new JsonObject();
        socketObj.addProperty("type", "openRequest");
        socketObj.addProperty("data", encryptData);
        socketObj.addProperty("message", cid + "(" + user.getName() + ") user requested opening.");
        RedisPublisher.publish("" + adminUser.getCid(), socketObj.toString());

        // Return success response
        baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        return baseResponseDto;
    }

    private void insertDoorOpenHistory(String cid, String extraData) {
        UserList adminUser = this.userListRepository.findFirstByCidAndState(Long.parseLong(cid), Constants.STATE_NORMAL).orElse(null);
        if (adminUser == null)
            return;
        long userId = adminUser.getId();
        UserRoleList userRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL).orElse(null);
        if (userRole == null)
            return;
        long deviceId = userRole.getDeviceId();

        // Insert Door Operate History
        DeviceOperateHistory newHistory = new DeviceOperateHistory();
        newHistory.setUserId(userId);
        newHistory.setDeviceId(deviceId);
        newHistory.setLinkId(adminUser.getId());
        newHistory.setAction(Constants.DEVICE_OPERATE_TYPE_OPEN);
        newHistory.setMode(Constants.DEVICE_OPERATE_MODE_PHONE);
        newHistory.setActionDateInt(Global.getDateIntValue());
        newHistory.setExtraData(extraData);
        this.deviceOperateHistoryRepository.save(newHistory);

        // Publish Redis queue
        List<UserRoleList> userRoleLists = this.userRoleListRepository.findAllByDeviceIdAndState(deviceId, Constants.STATE_NORMAL);
        for (UserRoleList userRoleList : userRoleLists) {
            UserList user = this.userListRepository.findById(userRoleList.getUserId()).orElse(null);
            if (user == null)
                continue;

            JsonObject socketObj = new JsonObject();
            socketObj.addProperty("type", "openHistory");
            socketObj.addProperty("message", "Door opened.");
            RedisPublisher.publish("" + user.getCid(), socketObj.toString());
        }
    }

    public BaseResponseDto history(String cid, String fromDate, String toDate) {
        String[] fromDateSplits = fromDate.split("-");
        int fromDateInt = Integer.parseInt(String.format("%02d", Integer.parseInt(fromDateSplits[0]) % 100) + String.format("%02d", Integer.parseInt(fromDateSplits[1])) + String.format("%02d", Integer.parseInt(fromDateSplits[2])));
        String[] toDateSplits = toDate.split("-");
        int toDateInt = Integer.parseInt(String.format("%02d", Integer.parseInt(toDateSplits[0]) % 100) + String.format("%02d", Integer.parseInt(toDateSplits[1])) + String.format("%02d", Integer.parseInt(toDateSplits[2])));

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        UserList user = this.userListRepository.findFirstByCidAndState(Long.parseLong(cid), Constants.STATE_NORMAL).orElse(null);
        if (user == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }
        long userId = user.getId();
        UserRoleList userRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL).orElse(null);
        if (userRole == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            return baseResponseDto;
        }
        long deviceId = userRole.getDeviceId();

        List<OperateHistoryProjection> result = this.deviceOperateHistoryRepository.ntGetOperateHistory(deviceId, fromDateInt, toDateInt);

        HistoryResponseDto resultDto = new HistoryResponseDto();
        resultDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        resultDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        resultDto.setData(result);
        return resultDto;
    }

    public BaseResponseDto userManageList(String cid, String password) {

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Check user
        UserList user = this.userListRepository.findFirstByCidAndState(Long.parseLong(cid), Constants.STATE_NORMAL).orElse(null);
        if (user == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }
        long userId = user.getId();
        // Check if admin
        UserRoleList userRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL).orElse(null);
        if (userRole == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            return baseResponseDto;
        }
        if (!userRole.getRoleType().equals(Constants.ROLE_TYPE_ADMIN)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            return baseResponseDto;
        }
        long deviceId = userRole.getDeviceId();

        // Search users
        List<UserRoleListProjection> result = this.userRoleListRepository.ntGetRoleListByDeviceId(deviceId);
        List<UserRoleListProjection> newResult = new ArrayList<>();
        for (UserRoleListProjection userRoleListProjection : result) {
            if (userRoleListProjection.getCid() != user.getCid() && !userRoleListProjection.getRoleType().equals(Constants.ROLE_TYPE_ADMIN))
                newResult.add(userRoleListProjection);
        }

        UserManageListResponseDto resultDto = new UserManageListResponseDto();
        resultDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        resultDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        resultDto.setData(newResult);
        return resultDto;
    }

    public BaseResponseDto userManageProcess(String cid, String password, long roleId, int state) {

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        // Check user
        UserList user = this.userListRepository.findFirstByCidAndState(Long.parseLong(cid), Constants.STATE_NORMAL).orElse(null);
        if (user == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_EXIST));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_EXIST));
            return baseResponseDto;
        }
        if (!user.getPassword().equals(password)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.INCORRECT_PASSWORD));
            return baseResponseDto;
        }
        long userId = user.getId();
        // Check if admin
        UserRoleList userRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL).orElse(null);
        if (userRole == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            return baseResponseDto;
        }
        if (!userRole.getRoleType().equals(Constants.ROLE_TYPE_ADMIN)) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            return baseResponseDto;
        }
        long deviceId = userRole.getDeviceId();

        // Get User Role
        userRole = this.userRoleListRepository.findById(roleId).orElse(null);
        if (userRole == null) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_ROLE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_ROLE));
            return baseResponseDto;
        }
        if (userRole.getDeviceId() != deviceId) {
            baseResponseDto.setRspCode(Constants.getLoginCode(Constants.LOGIN_CODES.NO_LINKED_DEVICE));
            baseResponseDto.setRspMsg(Constants.getLoginMessage(Constants.LOGIN_CODES.NO_LINKED_DEVICE));
            return baseResponseDto;
        }
        if (userRole.getState() == Constants.STATE_DELETE) {
            baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.FAILURE));
            baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.FAILURE));
            return baseResponseDto;
        }
        userRole.setState(state);
        this.userRoleListRepository.save(userRole);

        baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        return baseResponseDto;
    }

    public BaseResponseDto messageList(String cid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MessageListProjection> messageList = this.messageListRepository.ntGetMessageList(Long.parseLong(cid), pageable);
        if (messageList == null || messageList.getSize() == 0) {
            BaseResponseDto baseResponseDto = new BaseResponseDto();
            baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.NO_DATA));
            baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.NO_DATA));
            return baseResponseDto;
        }

        MessageListResponseDto responseDto = new MessageListResponseDto();
        responseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        responseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        responseDto.setTotalCount(messageList.getTotalElements());
        responseDto.setTotalPage(messageList.getTotalPages());
        responseDto.setHasNextPage(messageList.hasNext());
        responseDto.setHasPrevPage(messageList.hasPrevious());
        responseDto.setData(messageList.getContent());

        return responseDto;
    }

    public BaseResponseDto messageSend(String cid, String msg) {
        MessageList messageList = new MessageList();
        messageList.setFromCid(Long.parseLong(cid));
        messageList.setToCid(0L);
        messageList.setContent(msg);
        messageList.setState(Constants.STATE_NORMAL);
        this.messageListRepository.save(messageList);

        BaseResponseDto baseResponseDto = new BaseResponseDto();
        baseResponseDto.setRspCode(Constants.getDefaultCode(Constants.DEFAULT_CODES.SUCCESS));
        baseResponseDto.setRspMsg(Constants.getDefaultMessage(Constants.DEFAULT_CODES.SUCCESS));
        return baseResponseDto;
    }

    private void setClientAliveState(String cid, String type, String value) {
        UserList user = this.userListRepository.findFirstByCidAndState(Long.parseLong(cid), Constants.STATE_NORMAL).orElse(null);
        if (user == null)
            return;
        long userId = user.getId();
        UserRoleList userRole = this.userRoleListRepository.findFirstByUserIdAndState(userId, Constants.STATE_NORMAL).orElse(null);
        if (userRole == null)
            return;
        long deviceId = userRole.getDeviceId();
        DeviceInfoHistory deviceInfoHistory = this.deviceInfoHistoryRepository.findFirstByDeviceIdAndType(deviceId, type).orElse(null);
        if (deviceInfoHistory == null) {
            deviceInfoHistory = new DeviceInfoHistory();
            deviceInfoHistory.setDeviceId(deviceId);
            deviceInfoHistory.setType(type);
        }
        deviceInfoHistory.setValue(value);
        this.deviceInfoHistoryRepository.save(deviceInfoHistory);
    }


    public void processSocketMessage(String userId, String message) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);

            String type = jsonObject.get("type").getAsString();

            switch (type) {
                case "ping":  // Set Client Alive
                    setClientAliveState(userId, Constants.DEVICE_INFO_TYPE_SOCKET, "" + System.currentTimeMillis());
                    break;
                case "batteryInfo":
                    setClientAliveState(userId, Constants.DEVICE_INFO_TYPE_BLUETOOTH, "" + System.currentTimeMillis());
                    setClientAliveState(userId, Constants.DEVICE_INFO_TYPE_BATTERY, jsonObject.get("data").getAsString());
                    break;
                case "openHistory":
                    String data = jsonObject.get("data").getAsString();
                    insertDoorOpenHistory(userId, data);
                    break;
            }
        } catch (Exception ignored) {

        }
    }
}
