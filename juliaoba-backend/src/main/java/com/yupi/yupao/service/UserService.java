package com.yupi.yupao.service;

import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupao.model.vo.UserVO;
import com.yupi.yupao.model.vo.NearbyUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);
    
    /**
     * 根据标签搜索用户，并排除指定用户
     *
     * @param tagNameList 标签列表
     * @param excludeUserId 要排除的用户ID
     * @return 符合条件的用户列表
     */
    List<User> searchUsersByTags(List<String> tagNameList, Long excludeUserId);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配用户
     * @param num
     * @param loginUser
     * @return
     */
    List<User> matchUsers(long num, User loginUser);
    
    /**
     * 获取附近的用户
     * @param loginUser 当前登录用户
     * @param distance 搜索半径，单位千米
     * @return 附近用户列表
     */
    List<NearbyUserVO> getNearbyUsers(User loginUser, double distance);
}
