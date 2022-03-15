/*
    Copyright 2020-2022. Huawei Technologies Co., Ltd. All rights reserved.
    Licensed under the Apache License, Version 2.0 (the "License")
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        https://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.huawei.hms.nativescript.analitycs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.entity.AAIDResult;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HMSAnalyticsWrapper  {
    private static final String TAG = HMSAnalyticsWrapper.class.getSimpleName();
    private static HiAnalyticsInstance instance;

    private static HMSAnalyticsListener mAnalyticsListener;

    private enum LogLevel {
        DEBUG(3),
        INFO(4),
        WARN(5),
        ERROR(6);
        int intValue;

        LogLevel(int logLevel) {
            this.intValue = logLevel;
        }
    }

    public static void executeCallback(boolean isSuccess, String data) {
        if(mAnalyticsListener != null) {
            if(isSuccess) {
                mAnalyticsListener.success(data);
            } else {
                mAnalyticsListener.error(data);
            }
        }
    }

    public static void getAnalyticsInstanceId(Context context, HMSAnalyticsListener listener) {
        mAnalyticsListener = listener;
        executeCallback(true, HmsInstanceId.getInstance(context).getId());
    }

    public static void getAnalyticsInstance(Context context, String routePolicy) {
        instance = HiAnalytics.getInstance(context, routePolicy);
    }

    public static void pageStart(String pageName, String pageClassOverride) {
        instance.pageStart(pageName, pageClassOverride);
    }

    public static void pageEnd(String pageName) {
        instance.pageEnd(pageName);
    }

    public static void onEvent(String event, Bundle bundle) {
        instance.onEvent(event, bundle);
    }

    public static void setAnalyticsEnabled(boolean enabled) {
        instance.setAnalyticsEnabled(enabled);
    }

    public static void setUserId(String userId) throws IllegalArgumentException {
        instance.setUserId(userId);
    }

    public static void setUserProfile(String name, String value) {
        instance.setUserProfile(name, value);
    }

    public static void setPushToken(String token) {
        instance.setPushToken(token);
    }

    public static void setMinActivitySessions(long milliseconds) {
        instance.setMinActivitySessions(milliseconds);
    }

    public static void setSessionDuration(int milliseconds) {
        instance.setSessionDuration(milliseconds);
    }

    public static void clearCachedData() {
        instance.clearCachedData();
    }

    public static void getAAID(Context context, HMSAnalyticsListener listener) {
        mAnalyticsListener = listener;
        Task<AAIDResult> idResult = HmsInstanceId.getInstance(context).getAAID();
        idResult.addOnSuccessListener(new OnSuccessListener<AAIDResult>() {
            @Override
            public void onSuccess(AAIDResult aaidResult) {
                if(aaidResult.getId() != "") {
                    executeCallback(true, aaidResult.getId());
                } else {
                    executeCallback(false, "");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception myException) {
                executeCallback(false, "");
            }
        });
    }

    public static void isRestrictionEnabled() {
        Boolean result = instance.isRestrictionEnabled();
    }

    public static void addDefaultEventParams(Bundle bundle) {
        instance.addDefaultEventParams(bundle);
    }

    public static void setRestrictionEnabled(Boolean enabled) {
        instance.setRestrictionEnabled(enabled);
    }

    public static void setCollectAdsIdEnabled(boolean isEnabled) {
        instance.setCollectAdsIdEnabled(isEnabled);
    }

    //HiAnalyticsTools

    public static void enableLog() {
        HiAnalyticsTools.enableLog();
    }

    public static void enableLogWithLevel(String level) {
        HiAnalyticsTools.enableLog(LogLevel.valueOf(level).intValue);
    }

}
