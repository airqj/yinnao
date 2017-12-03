/**
 * Copyright 2017 Kailash Dabhi (Kingbull Technology)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.qjb.yinnao

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.RandomAccessFile

/**
 * `Wav` is recorder for recording audio in wav format.
 *
 * @author Kailash Dabhi
 * @date 31-07-2016
 */
public class Wav(f:File) {
    val file = f
    fun writeWavHeader() {
        val wavFile = randomAccessFile(file)
        wavFile.seek(0) // to the beginning
        wavFile.write(WavHeader(wavFile?.length()).toBytes())
        wavFile?.close()
    }

    private fun randomAccessFile(file: File): RandomAccessFile {
        val randomAccessFile: RandomAccessFile
        try {
            randomAccessFile = RandomAccessFile(file, "rw")
        } catch (e: FileNotFoundException) {
            throw RuntimeException(e)
        }

        return randomAccessFile
    }
}