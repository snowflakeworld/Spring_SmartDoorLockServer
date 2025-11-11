package spring.restapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.restapi.response_dto.BaseResponseDto;
import spring.restapi.resquest_dto.*;
import spring.restapi.service.DoorService;

import javax.validation.Valid;

@RestController
@RequestMapping("/smart_app/")
public class DoorController {

    private final DoorService doorService;

    public DoorController(DoorService doorService) {
        this.doorService = doorService;
    }

    /***
     * User Login
     *
     * @param requestDto cid, hashed password
     * @return login responseDto cid, name, gender, birthday, citizenNumber, installPlace, roleType, state, districtId, districtInfo, detailInfo, convDetailInfo, deviceId, propertyInfo
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponseDto> loginUser(@Valid @RequestBody LoginRequestDto requestDto) {
        BaseResponseDto response = this.doorService.login(requestDto.getCid(), requestDto.getPassword());

        return ResponseEntity.ok(response);
    }

    /***
     * Check register availability
     *
     * @param requestDto cid
     * @return base responseDto rspCode, rspMsg
     */
    @PostMapping("/check_register")
    public ResponseEntity<BaseResponseDto> checkRegisterUser(@Valid @RequestBody CheckRegisterRequestDto requestDto) {
        BaseResponseDto response = this.doorService.checkRegisterUser(requestDto.getCid());

        return ResponseEntity.ok(response);
    }

    /***
     * User Register
     *
     * @param requestDto cid, name, hashed password, gender, birthday, citizenNumber, installPlace, districtId, unit, floor, index, businessId, authType
     * @return base responseDto rspCode, rspMsg
     */
    @PostMapping("/register")
    public ResponseEntity<BaseResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto requestDto) {
        BaseResponseDto response = this.doorService.registerUser(requestDto.getCid(), requestDto.getName(), requestDto.getPassword(), requestDto.getGender(), requestDto.getBirthday(), requestDto.getCitizenNumber(), requestDto.getInstallPlace(), requestDto.getDistrictId(), requestDto.getUnit(), requestDto.getFloor(), requestDto.getIndex(), requestDto.getBusinessId(), requestDto.getAuthType());

        return ResponseEntity.ok(response);
    }

    /***
     * Search district data when registering
     *
     * @param requestDto districtId
     * @return searchDistrict responseDto id, name, parentId, [id, name, parentId, displayOrder, hasChild, state]
     */
    @PostMapping("/search_district")
    public ResponseEntity<BaseResponseDto> searchDistrict(@Valid @RequestBody SearchDistrictRequestDto requestDto) {
        BaseResponseDto response = this.doorService.searchDistrict(requestDto.getDistrictId());

        return ResponseEntity.ok(response);
    }

    /***
     * Get Business list
     *
     * @return businessList responseDto [id, name, displayOrder, state]
     */
    @PostMapping("/get_business_list")
    public ResponseEntity<BaseResponseDto> getBusinessList() {
        BaseResponseDto response = this.doorService.getBusinessList();

        return ResponseEntity.ok(response);
    }

    /***
     * Change User Info
     *
     * @param requestDto cid, name, password, gender, birthday, citizenNumber
     * @return base responseDto
     */
    @PostMapping("/change_userinfo")
    public ResponseEntity<BaseResponseDto> changeUserinfo(@Valid @RequestBody ChangeUserinfoRequestDto requestDto) {
        BaseResponseDto response = this.doorService.changeUserinfo(requestDto.getCid(), requestDto.getName(), requestDto.getPassword(), requestDto.getGender(), requestDto.getBirthday(), requestDto.getCitizenNumber());

        return ResponseEntity.ok(response);
    }

    /***
     * Change User Password
     *
     * @param requestDto cid, curPassword, newPassword
     * @return base responseDto
     */
    @PostMapping("/change_password")
    public ResponseEntity<BaseResponseDto> changePassword(@Valid @RequestBody ChangePasswordRequestDto requestDto) {
        BaseResponseDto response = this.doorService.changePassword(requestDto.getCid(), requestDto.getCurPassword(), requestDto.getNewPassword());

        return ResponseEntity.ok(response);
    }

    /***
     * Request Admin Authorization
     *
     * @param requestDto cid, password, deviceId
     * @return base responseDto
     */
    @PostMapping("/request_admin_auth")
    public ResponseEntity<BaseResponseDto> requestAdminAuth(@Valid @RequestBody RequestAdminAuthRequestDto requestDto) {
        BaseResponseDto response = this.doorService.requestAdminAuth(requestDto.getCid(), requestDto.getPassword(), requestDto.getDeviceId());

        return ResponseEntity.ok(response);
    }

    /***
     * Manage users by admin
     *
     * @param requestDto cid, password
     * @return userManage responseDto [id, cid, name, roleType, state, updateAt]
     */
    @PostMapping("/user_manage_list")
    public ResponseEntity<BaseResponseDto> userManageList(@Valid @RequestBody UserManageListRequestDto requestDto) {
        BaseResponseDto response = this.doorService.userManageList(requestDto.getCid(), requestDto.getPassword());

        return ResponseEntity.ok(response);
    }

    /***
     * Manage users by admin
     *
     * @param requestDto cid, password
     * @return userManage responseDto [id, cid, name, roleType, state, updateAt]
     */
    @PostMapping("/user_manage_process")
    public ResponseEntity<BaseResponseDto> userManageProcess(@Valid @RequestBody UserManageProcessRequestDto requestDto) {
        BaseResponseDto response = this.doorService.userManageProcess(requestDto.getCid(), requestDto.getPassword(), requestDto.getRoleId(), requestDto.getState());

        return ResponseEntity.ok(response);
    }

    /***
     * Check Door States(Socket connection, Bluetooth connection, Battery info)
     *
     * @param requestDto cid, hashed password, deviceId
     * @return checkStates responseDto socketConnected, doorConnected, batteryLevel
     */
    @PostMapping("/check_door_states")
    public ResponseEntity<BaseResponseDto> checkDoorStates(@Valid @RequestBody CheckStatesRequestDto requestDto) {
        BaseResponseDto response = this.doorService.checkDoorStates(requestDto.getCid(), requestDto.getPassword(), requestDto.getDeviceId());

        return ResponseEntity.ok(response);
    }

    /***
     * Request door opening by user or admin via network
     * Encrypts bluetooth communication data and send it to admin
     *
     * @param requestDto cid, hashed password
     * @return base responseDto
     */
    @PostMapping("/open_door_request")
    public ResponseEntity<BaseResponseDto> openDoor(@Valid @RequestBody OpenDoorRequestDto requestDto) {
        BaseResponseDto response = this.doorService.openDoorRequest(requestDto.getCid(), requestDto.getPassword());

        return ResponseEntity.ok(response);
    }

    /***
     * Get Door Open History
     *
     * @param requestDto cid, fromDate, toDate
     * @return history responseDto [id, name, action, mode, createAt]
     */
    @PostMapping("/history")
    public ResponseEntity<BaseResponseDto> history(@Valid @RequestBody HistoryRequestDto requestDto) {
        BaseResponseDto response = this.doorService.history(requestDto.getCid(), requestDto.getFromDate(), requestDto.getToDate());

        return ResponseEntity.ok(response);
    }

    /***
     * Get Feedback message list
     *
     * @param requestDto cid, page, size
     * @return messageList responseDto totalCount, totalPage, hasNextPage, hasPrevPage, [id, isUser, msg, createAt]
     */
    @PostMapping("/message_list")
    public ResponseEntity<BaseResponseDto> messageList(@Valid @RequestBody MessageListRequestDto requestDto) {
        BaseResponseDto response = this.doorService.messageList(requestDto.getCid(), requestDto.getPage(), requestDto.getSize());

        return ResponseEntity.ok(response);
    }

    /***
     * Send Feedback message
     *
     * @param requestDto cid, msg
     * @return base responseDto
     */
    @PostMapping("/message_send")
    public ResponseEntity<BaseResponseDto> messageSend(@Valid @RequestBody MessageSendRequestDto requestDto) {
        BaseResponseDto response = this.doorService.messageSend(requestDto.getCid(), requestDto.getMsg());

        return ResponseEntity.ok(response);
    }

    /***
     * Check Feedback message
     *
     * @param requestDto cid, msg
     * @return base responseDto
     */
    @PostMapping("/message_check")
    public ResponseEntity<BaseResponseDto> messageCheck(@Valid @RequestBody MessageSendRequestDto requestDto) {
        BaseResponseDto response = this.doorService.messageSend(requestDto.getCid(), requestDto.getMsg());

        return ResponseEntity.ok(response);
    }
}
