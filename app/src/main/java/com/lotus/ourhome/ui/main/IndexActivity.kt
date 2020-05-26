package com.lotus.ourhome.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import com.lotus.ourhome.R
import com.lotus.ourhome.base.CheckPermissionActivity
import com.lotus.ourhome.utils.ToastUtil
import java.util.*

class IndexActivity : CheckPermissionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkPermission()) {
            checkToMain()
        }
    }

    fun checkToMain() {
        Handler().postDelayed(Runnable {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUSET_CODE_OVER_DRAW) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    if (checkPermission()) {
                        checkToMain()
                    }
                }
            }, 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_FOR_MULTIPLE_PERMISSION && grantResults.isNotEmpty()) {
            var isAllGranted = true
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false
                    break
                }
            }
            if (!isAllGranted) {
                ToastUtil.showToast(getString(R.string.system_permission_login_need_all_granted))
                finish()
            } else {
                checkToMain()
            }
        }
    }
}