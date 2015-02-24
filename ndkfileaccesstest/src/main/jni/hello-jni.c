/*
 * Copyright (C) 2009 The Android Open Source Project
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
 *
 */
#include <string.h>
#include <jni.h>
#include <stdio.h>
#include <android/log.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define TAG "NDKFileAccessTest (native)"



jchar
Java_com_logitud_ndkfileaccesstest_NDKFileAccessTest_writeToFileWithoutODirect( JNIEnv* env,
                                                  jobject thiz, jstring path )
{
    const char *nativePath = (*env)->GetStringUTFChars(env, path, 0);
    __android_log_print(ANDROID_LOG_INFO, TAG, "The path passed to the native c is %s, attempting to create file with open", nativePath);

    int fileWrite = open(nativePath, O_CREAT | O_RDWR);
    __android_log_print(ANDROID_LOG_INFO, TAG, "open result fileWrite = %d", fileWrite);

    if (fileWrite) {
        ssize_t writtenBytes;
        char bytes[1];

        writtenBytes= write(fileWrite, "a", 1);
        __android_log_print(ANDROID_LOG_INFO, TAG, "writtenBytes a = %d", writtenBytes);

        close(fileWrite);

        if (writtenBytes == -1) {
            return 0x00;
        } else {
            return 0x01;
        }

    } else {
        return 0x00;
    }
}

jchar
Java_com_logitud_ndkfileaccesstest_NDKFileAccessTest_writeToFileWithODirect( JNIEnv* env,
                                                  jobject thiz, jstring path )
{
    const char *nativePath = (*env)->GetStringUTFChars(env, path, 0);
    __android_log_print(ANDROID_LOG_INFO, TAG, "The path passed to the native c is %s, attempting to create file with open and O_DIRECT", nativePath);

    int fileWrite = open(nativePath, O_CREAT | O_RDWR | O_DIRECT);

    __android_log_print(ANDROID_LOG_INFO, TAG, "open result fileWrite = %d", fileWrite);

    if (fileWrite)
    {
        ssize_t writtenBytes;
        char* buffer;
        char bytes[1];

        buffer= memalign(512,512);
        if(!buffer) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Buffer allocation failed");
        }

        memset(buffer, 0, 512);

        *buffer= 'a';
        writtenBytes= write(fileWrite, buffer, 512);
        __android_log_print(ANDROID_LOG_INFO, TAG, "writtenBytes a = %d", writtenBytes);

        close(fileWrite);

        if (writtenBytes == -1) {
            return 0x00;
        } else {
            return 0x01;
        }
    } else {
        return 0x00;
    }
}




jchar
Java_com_logitud_ndkfileaccesstest_NDKFileAccessTest_writeToFileWithODirect2( JNIEnv* env,
                                                  jobject thiz, jstring path )
{
    const char *nativePath = (*env)->GetStringUTFChars(env, path, 0);
    __android_log_print(ANDROID_LOG_INFO, TAG, "The path passed to the native c is %s, attempting to create file with open and O_DIRECT", nativePath);
    int fileWrite = open(nativePath, O_CREAT | O_RDWR | O_DIRECT);
    int fileRead = open(nativePath, O_RDWR | O_DIRECT);

    __android_log_print(ANDROID_LOG_INFO, TAG, "open result fileWrite = %d", fileWrite);
    __android_log_print(ANDROID_LOG_INFO, TAG, "open result fileRead = %d", fileRead);

    if (fileWrite && fileRead)
    {
        ssize_t writtenBytes, readBytes;
        char* buffer, *readBuffer;
        char bytes[1];
        int diff;

        buffer= memalign(512,512);
        readBuffer= memalign(512,512);
        if(!buffer || !readBuffer) {
            __android_log_print(ANDROID_LOG_INFO, TAG, "Buffer allocation failed");
        }
        memset(buffer, 0, 512);
        memset(readBuffer, 1, 512);
        *buffer= 'a';
        writtenBytes= write(fileWrite, buffer, 512);
        readBytes= read(fileRead, readBuffer, 512);
        diff= memcmp(buffer, readBuffer, 512);
        __android_log_print(ANDROID_LOG_INFO, TAG, "writtenBytes a = %d, readBytes= %d", writtenBytes, readBytes);
        __android_log_print(ANDROID_LOG_INFO, TAG, "diff a = %d", diff);

        *buffer= 'b';
        writtenBytes= write(fileWrite, buffer, 512);
        readBytes= read(fileRead, readBuffer, 512);
        diff= memcmp(buffer, readBuffer, 512);
        __android_log_print(ANDROID_LOG_INFO, TAG, "writtenBytes a = %d, readBytes= %d", writtenBytes, readBytes);
        __android_log_print(ANDROID_LOG_INFO, TAG, "diff b = %d", diff);


        close(fileWrite);
        close(fileRead);

        return 0x01;
    } else {
        return 0x00;
    }
}