package com.thumbsupec.lib_base.web


object WebUrl {
    private const val USER_BASE_URL = "http://api.ispruz.com/"

    private const val USER_BASE_URL3 = "http://support.ispruz.com/app/"

    // 用户协议
    fun SERVICE_URL() =
        USER_BASE_URL + "documentApi/getDocument?type=ServiceAgreement&lang="

    // 隐私政策
    fun POLICY_URL() =
        USER_BASE_URL + "documentApi/getDocument?type=PrivacyPolicy&lang="

    // 隐私设置
    fun POLICY_SETTING_URL() =
        USER_BASE_URL + "documentApi/getDocument?type=PrivacySetting&lang="

    // 电子说明书
    fun USER_MANUAL_URL() =
        USER_BASE_URL + "documentApi/getDocument?type=UserManual&lang=&model="

    // 使用帮助
    fun HELP_URL() =
        USER_BASE_URL3 + "faqClassifyList?lang=&accountId="

    // 积分规则
    fun INTEGRAL_RULES() =
        USER_BASE_URL + "documentApi/getDocument?type=integral-usergiud&lang="
}