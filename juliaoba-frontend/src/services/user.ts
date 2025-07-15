import myAxios from "../plugins/myAxios";
import { setCurrentUserState } from "../states/user";

export const getCurrentUser = async () => {
    // const currentUser = getCurrentUserState();
    // if (currentUser) {
    //     return currentUser;
    // }
    // 不存在则从远程获取
    const res = await myAxios.get('/user/current');
    if (res.code === 0) {
        setCurrentUserState(res.data);
        return res.data;
    }
    return null;
}

/**
 * 用户退出登录
 */
export const userLogout = async () => {
    const res = await myAxios.post('/user/logout');
    if (res.code === 0) {
        return true;
    }
    return false;
}

