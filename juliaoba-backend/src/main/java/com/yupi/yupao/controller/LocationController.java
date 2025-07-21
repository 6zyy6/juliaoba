package com.yupi.yupao.controller;

import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import com.yupi.yupao.model.domain.User;
import com.yupi.yupao.model.request.LocationUpdateRequest;
import com.yupi.yupao.model.vo.NearbyUserVO;
import com.yupi.yupao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 地理位置接口
 */
@RestController
@RequestMapping("/location")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class LocationController {
    
    @Resource
    private UserService userService;

    /**
     * 更新用户位置
     *
     * @param locationUpdateRequest 位置更新请求
     * @param request HTTP请求
     * @return 更新结果
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateLocation(@RequestBody LocationUpdateRequest locationUpdateRequest,
                                               HttpServletRequest request) {
        if (locationUpdateRequest == null || locationUpdateRequest.getLatitude() == null 
                || locationUpdateRequest.getLongitude() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "位置信息不完整");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        
        // 更新位置信息
        User userToUpdate = new User();
        userToUpdate.setId(loginUser.getId());
        userToUpdate.setLatitude(locationUpdateRequest.getLatitude());
        userToUpdate.setLongitude(locationUpdateRequest.getLongitude());
        userToUpdate.setLastLocationUpdateTime(new Date());
        
        boolean result = userService.updateById(userToUpdate);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取附近的用户
     *
     * @param distance 距离，单位千米，默认5千米
     * @param request HTTP请求
     * @return 附近用户列表
     */
    @GetMapping("/nearby")
    public BaseResponse<List<NearbyUserVO>> getNearbyUsers(
            @RequestParam(defaultValue = "5") double distance,
            HttpServletRequest request) {
        
        if (distance <= 0 || distance > 100) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "距离参数不合法，应为0-100千米");
        }
        
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser.getLatitude() == null || loginUser.getLongitude() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户位置未更新，请先更新位置");
        }
        
        List<NearbyUserVO> nearbyUsers = userService.getNearbyUsers(loginUser, distance);
        return ResultUtils.success(nearbyUsers);
    }
} 