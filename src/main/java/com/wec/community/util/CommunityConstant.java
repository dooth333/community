package com.wec.community.util;

public interface CommunityConstant {
    /***
     * 激活成功
     */
    int ACTIVSYION_SUCCESS = 0;

    /***
     * 重复激活
     */
    int ACTIVSYION_REPEAT = 1;

    /***
     * 激活失败
     */
    int ACTIVSYION_FAILURE = 2;

    /***
     * 默认超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /***
     * 记住状态下登陆凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 *100;
}
