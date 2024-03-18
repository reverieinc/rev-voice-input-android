/*
 * All Rights Reserved. Copyright 2023. Reverie Language Technologies Limited.(https://reverieinc.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reverie.voiceinput.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.reverie.voiceinput.LOG.Companion.customLogger

/**
 * Class to verify the permission access given in the application
 */
class PermissionUtils {
    companion object {

        private const val TAG = "PermissionUtils"
        fun checkPermissionsAudio(mContext:Context): Boolean {
            val isPermsGranted: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mContext!!.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

            } else {
                true
            }
            if (!isPermsGranted) {

                return false
            }
            return true
        }
        fun checkStoragePermissions(context: Context): Boolean {

            var isPermsGranted: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isPermsGranted =
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    isPermsGranted =
                        context.checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
                }

            } else {
                //Automatically granted for lower versions
                isPermsGranted = true
            }


            if (!isPermsGranted) {
                return false
            }



            return true
        }

        public fun isInternetAvailable(context: Context): Boolean {
            var result = false
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
                result = when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                @Suppress("DEPRECATION")
                connectivityManager.run {
                    connectivityManager.activeNetworkInfo?.run {
                        result = when (type) {
                            ConnectivityManager.TYPE_WIFI -> true
                            ConnectivityManager.TYPE_MOBILE -> true
                            ConnectivityManager.TYPE_ETHERNET -> true
                            else -> false
                        }

                    }
                }
            }
            return result
        }

        fun checkManifestPermissions(context: Context, vararg reqdPerms: String): Boolean {
            val packageInfo: PackageInfo

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                packageInfo = context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_PERMISSIONS
                )
            }


            val permissions = packageInfo.requestedPermissions


            if (permissions.isNullOrEmpty())
                return false


            for (eachPerm in reqdPerms) {
                if (eachPerm in permissions)
                    continue
                else {
                    customLogger(TAG, "checkManifestPermissions: failed for= $eachPerm")
                    return false
                }
            }


            return true
        }


    }
}