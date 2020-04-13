package cn.ifafu.ifafu.data.bean

import androidx.annotation.StringDef
import cn.ifafu.ifafu.data.entity.User

@MustBeDocumented
@Target(
        AnnotationTarget.TYPE_PARAMETER,
        AnnotationTarget.FIELD
)
@StringDef(value = [User.FAFU, User.FAFU_JS])
annotation class SchoolCode