package com.shudong.lib_base.base.viewmodel

import com.thumbsupec.lib_net.di.HEADERURL

const val VERIFICATION_CODE = "${HEADERURL}non-auth/getVerifyCode"
const val CHECK_CODE = "${HEADERURL}non-auth/checkVerifyCode"

// 用户信息接口
const val USERINFO = "${HEADERURL}auth/getMemberInfo"

// 分享周报
const val SHARE_WEEK_REPORT = "${HEADERURL}auth/shareReportTask"

// 同步刷牙数据
const val SYNC_BRUSH_DATA = "${HEADERURL}auth/syncDailyData"

// 修改用户信息
const val MODIFY_USER_INFO = "${HEADERURL}auth/setMemberInfo"