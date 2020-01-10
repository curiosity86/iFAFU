package cn.ifafu.ifafu.mvp.elec_login

import android.graphics.Bitmap

interface UIEvent {
    suspend fun refreshVerify(bitmap: Bitmap)
}