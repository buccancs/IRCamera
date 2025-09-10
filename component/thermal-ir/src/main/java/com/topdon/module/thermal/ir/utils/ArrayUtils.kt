package com.topdon.module.thermal.ir.utils

object ArrayUtils {

    /**
     * utility(utility)-Rotateutility
     * @param rotateType 1:Rotate90 2:Rotate180  3:Rotate270
     */
    fun getMaxIndex(
        data: FloatArray,
        rotateType: Int = 0,
        selectIndexList: ArrayList<Int> = arrayListOf()
    ): Int {
        val index = when (rotateType) {
            1, 2, 3 -> getRotateMaxIndex(data, rotateType, selectIndexList)
            else -> getMaxIndex(data, selectIndexList)
        }
        return index
    }

    /**
     * utility(utility)-Rotateutility
     * @param rotateType 1:Rotate90 2:Rotate180  3:Rotate270
     */
    fun getMinIndex(
        data: FloatArray,
        rotateType: Int = 0,
        selectIndexList: ArrayList<Int> = arrayListOf()
    ): Int {
        val index = when (rotateType) {
            1, 2, 3 -> getRotateMinIndex(data, rotateType, selectIndexList)
            else -> getMinIndex(data, selectIndexList)
        }
        return index
    }

    /**
     * Rotateutility
     * @param rotateType 1:Rotate90 2:Rotate180  3:Rotate270
     */
    fun matrixRotate(srcData: FloatArray, rotateType: Int = 0): FloatArray {
        return when (rotateType) {
            1 -> matrixRotate90(srcData)
            2 -> matrixRotate180(srcData)
            3 -> matrixRotate270(srcData)
            else -> srcData
        }
    }

    /**
     * utility(utility)-utility
     */
    private fun getMaxIndex(
        data: FloatArray,
        selectIndexList: ArrayList<Int> = arrayListOf()
    ): Int {
        if (selectIndexList.size == 0) {
            // Utility functionSpecifiedutility
            var maxIndex = 0
            for (i in 1 until data.size - 1) {
                if (data[i] > data[maxIndex]) {
                    maxIndex = i
                }
            }
            return maxIndex
        } else {
            val selectPoint = FloatArray(selectIndexList.size)
            for (i in 0 until selectIndexList.size) {
                selectPoint[i] = data[selectIndexList[i]]
            }
            var maxIndex = 0
            for (i in 1 until selectPoint.size - 1) {
                if (selectPoint[i] > selectPoint[maxIndex]) {
                    maxIndex = i
                }
            }
            return selectIndexList[maxIndex]
        }
    }


    /**
     * utility(utility)-utility
     */
    private fun getMinIndex(
        data: FloatArray,
        selectIndexList: ArrayList<Int> = arrayListOf()
    ): Int {
        if (selectIndexList.size == 0) {
            var minIndex = 0
            for (i in 1 until data.size - 1) {
                if (data[i] == 0f) {
                    continue
                }
                if (data[i] < data[minIndex]) {
                    minIndex = i
                }
            }
            return minIndex
        } else {
            val selectPoint = FloatArray(selectIndexList.size)
            for (i in 0 until selectIndexList.size) {
                selectPoint[i] = data[selectIndexList[i]]
            }
            var minIndex = 0
            for (i in 1 until selectPoint.size - 1) {
                if (selectPoint[i] == 0f) {
                    continue
                }
                if (selectPoint[i] < selectPoint[minIndex]) {
                    minIndex = i
                }
            }
            return selectIndexList[minIndex]
        }
    }

    /**
     * utility(utility)-Rotateutility
     * @param rotateType 1:Rotate90 2:Rotate180  3:Rotate270
     */
    private fun getRotateMaxIndex(
        data: FloatArray,
        rotateType: Int = 0,
        selectIndexList: ArrayList<Int> = arrayListOf()
    ): Int {
        if (selectIndexList.size == 0) {
            val destData = matrixRotate(data, rotateType)
            var maxIndex = 0
            for (i in 1 until destData.size - 1) {
                if (destData[i] > destData[maxIndex]) {
                    maxIndex = i
                }
            }
            return maxIndex
        } else {
            val destData = matrixRotate(data, rotateType)
            val selectPoint = FloatArray(selectIndexList.size)
            for (i in 0 until selectIndexList.size) {
                selectPoint[i] = destData[selectIndexList[i]]
            }
            var maxIndex = 0
            for (i in 1 until selectPoint.size - 1) {
                if (selectPoint[i] > selectPoint[maxIndex]) {
                    maxIndex = i
                }
            }
            return selectIndexList[maxIndex]
        }
    }

    /**
     * utility(utility)-Rotateutility
     * @param rotateType 1:Rotate90 2:Rotate180  3:Rotate270
     */
    private fun getRotateMinIndex(
        data: FloatArray,
        rotateType: Int = 0,
        selectIndexList: ArrayList<Int> = arrayListOf()
    ): Int {
        if (selectIndexList.size == 0) {
            val destData = matrixRotate(data, rotateType)
            var minIndex = 0
            for (i in 1 until destData.size - 1) {
                if (destData[i] == 0f) {
                    continue
                }
                if (destData[i] < destData[minIndex]) {
                    minIndex = i
                }
            }
            return minIndex
        } else {
            val destData = matrixRotate(data, rotateType)
            val selectPoint = FloatArray(selectIndexList.size)
            for (i in 0 until selectIndexList.size) {
                selectPoint[i] = destData[selectIndexList[i]]
            }
            var minIndex = 0
            for (i in 1 until selectPoint.size - 1) {
                if (selectPoint[i] == 0f) {
                    continue
                }
                if (selectPoint[i] < selectPoint[minIndex]) {
                    minIndex = i
                }
            }
            return selectIndexList[minIndex]
        }
    }

    /**
     * utilityRotate90°
     */
    private fun matrixRotate90(srcData: FloatArray): FloatArray {
        val row = 192
        val column = 256
        val srcMatrix = Array(row) { FloatArray(column) }
        for (i in 0 until row) {
            for (j in 0 until column) {
                srcMatrix[i][j] = srcData[i * column + j]
            }
        }
        val destMatrix = Array(column) { FloatArray(row) }
        for (x in 0 until column) {
            for (y in 0 until row) {
                destMatrix[x][y] = srcMatrix[row - 1 - y][x]// Utility functionRotate90utility
            }
        }
        val data = FloatArray(srcData.size)
        for (i in destMatrix.indices) {
            for (j in destMatrix[i].indices) {
                data[destMatrix[0].size * i + j] = destMatrix[i][j]
            }
        }
        return data
    }

    /**
     * utilityRotate180°
     */
    private fun matrixRotate180(srcData: FloatArray): FloatArray {
        val row = 192
        val column = 256
        val srcMatrix = Array(row) { FloatArray(column) }
        for (i in 0 until row) {
            for (j in 0 until column) {
                srcMatrix[i][j] = srcData[i * column + j]
            }
        }
        val destMatrix = Array(row) { FloatArray(column) }
        for (x in 0 until row) {
            for (y in 0 until column) {
                destMatrix[x][y] = srcMatrix[row - 1 - x][column - 1 - y]// Utility functionRotate180utility
            }
        }
        val data = FloatArray(srcData.size)
        for (i in destMatrix.indices) {
            for (j in destMatrix[i].indices) {
                data[destMatrix[0].size * i + j] = destMatrix[i][j]
            }
        }
        return data
    }

    /**
     * utilityRotate270°
     * utilityRotateutility
     */
    private fun matrixRotate270(srcData: FloatArray): FloatArray {
        val row = 192
        val column = 256
        val srcMatrix = Array(row) { FloatArray(column) }// Utility function
        for (i in 0 until row) {
            for (j in 0 until column) {
                srcMatrix[i][j] = srcData[i * column + j]
            }
        }
        val destMatrix = Array(column) { FloatArray(row) }// Utility function
        for (x in 0 until column) {
            for (y in 0 until row) {
                destMatrix[x][y] = srcMatrix[y][column - 1 - x]// Utility functionRotate270utility
            }
        }
        val data = FloatArray(srcData.size)
        for (i in destMatrix.indices) {
            for (j in destMatrix[i].indices) {
                data[destMatrix[0].size * i + j] = destMatrix[i][j]
            }
        }
        return data
    }

}