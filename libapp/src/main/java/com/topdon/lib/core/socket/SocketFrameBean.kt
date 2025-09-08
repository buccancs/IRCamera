package com.topdon.lib.core.socket

 * TC007 Socket .
 * @param isMaxShow
 * @param isMinShow
 * @param isCenterShow
 * @param maxX  X
 * @param maxY  Y
 * @param maxValue *10
 * @param minX  X
 * @param minY  Y
 * @param minValue *10
 * @param centerX  X
 * @param centerY  Y
 * @param centerValue *10
 * @param isMaxWarn
 * @param isMinWarn
 * @param isCenterWarn
 * @param isP1Show 1
 * @param p1X 1 X
 * @param p1Y 1 Y
 * @param p1Value 1*10
 * @param isP1MaxWarn 1
 * @param isP1MinWarn 1
 * @param isP1CenterWarn 1
 * @param isL1Show 1
 * @param l1StartX 1 X
 * @param l1StartY 1 Y
 * @param l1EndX 1 X
 * @param l1EndY 1 Y
 * @param l1MaxX 1 X
 * @param l1MaxY 1 Y
 * @param l1MaxValue 1*10
 * @param l1MinX 1 X
 * @param l1MinY 1 Y
 * @param l1MinValue 1*10
 * @param l1AveValue 1*10
 * @param isL1MaxWarn 1
 * @param isL1MinWarn 1
 * @param isL1CenterWarn 1
data class SocketFrameBean(
    /** isMaxShow property */
    val isMaxShow: Boolean,
    /** isMinShow property */
    val isMinShow: Boolean,
    /** isCenterShow property */
    val isCenterShow: Boolean,
    /** maxX property */
    val maxX: Int,
    /** maxY property */
    val maxY: Int,
    /** maxValue property */
    val maxValue: Int,
    /** minX property */
    val minX: Int,
    /** minY property */
    val minY: Int,
    /** minValue property */
    val minValue: Int,
    /** centerX property */
    val centerX: Int,
    /** centerY property */
    val centerY: Int,
    /** centerValue property */
    val centerValue: Int,
    /** isMaxWarn property */
    val isMaxWarn: Boolean,
    /** isMinWarn property */
    val isMinWarn: Boolean,
    /** isCenterWarn property */
    val isCenterWarn: Boolean,

    /** isP1Show property */
    val isP1Show: Boolean,
    /** p1X property */
    val p1X: Int,
    /** p1Y property */
    val p1Y: Int,
    /** p1Value property */
    val p1Value: Int,
    /** isP1MaxWarn property */
    val isP1MaxWarn: Boolean,
    /** isP1MinWarn property */
    val isP1MinWarn: Boolean,
    /** isP1CenterWarn property */
    val isP1CenterWarn: Boolean,
    /** isP2Show property */
    val isP2Show: Boolean,
    /** p2X property */
    val p2X: Int,
    /** p2Y property */
    val p2Y: Int,
    /** p2Value property */
    val p2Value: Int,
    /** isP2MaxWarn property */
    val isP2MaxWarn: Boolean,
    /** isP2MinWarn property */
    val isP2MinWarn: Boolean,
    /** isP2CenterWarn property */
    val isP2CenterWarn: Boolean,
    /** isP3Show property */
    val isP3Show: Boolean,
    /** p3X property */
    val p3X: Int,
    /** p3Y property */
    val p3Y: Int,
    /** p3Value property */
    val p3Value: Int,
    /** isP3MaxWarn property */
    val isP3MaxWarn: Boolean,
    /** isP3MinWarn property */
    val isP3MinWarn: Boolean,
    /** isP3CenterWarn property */
    val isP3CenterWarn: Boolean,

    /** isL1Show property */
    val isL1Show: Boolean,
    /** l1StartX property */
    val l1StartX: Int,
    /** l1StartY property */
    val l1StartY: Int,
    /** l1EndX property */
    val l1EndX: Int,
    /** l1EndY property */
    val l1EndY: Int,
    /** l1MaxX property */
    val l1MaxX: Int,
    /** l1MaxY property */
    val l1MaxY: Int,
    /** l1MaxValue property */
    val l1MaxValue: Int,
    /** l1MinX property */
    val l1MinX: Int,
    /** l1MinY property */
    val l1MinY: Int,
    /** l1MinValue property */
    val l1MinValue: Int,
    /** l1AveValue property */
    val l1AveValue: Int,
    /** isL1MaxWarn property */
    val isL1MaxWarn: Boolean,
    /** isL1MinWarn property */
    val isL1MinWarn: Boolean,
    /** isL1CenterWarn property */
    val isL1CenterWarn: Boolean,
    /** isL2Show property */
    val isL2Show: Boolean,
    /** l2StartX property */
    val l2StartX: Int,
    /** l2StartY property */
    val l2StartY: Int,
    /** l2EndX property */
    val l2EndX: Int,
    /** l2EndY property */
    val l2EndY: Int,
    /** l2MaxX property */
    val l2MaxX: Int,
    /** l2MaxY property */
    val l2MaxY: Int,
    /** l2MaxValue property */
    val l2MaxValue: Int,
    /** l2MinX property */
    val l2MinX: Int,
    /** l2MinY property */
    val l2MinY: Int,
    /** l2MinValue property */
    val l2MinValue: Int,
    /** l2AveValue property */
    val l2AveValue: Int,
    /** isL2MaxWarn property */
    val isL2MaxWarn: Boolean,
    /** isL2MinWarn property */
    val isL2MinWarn: Boolean,
    /** isL2CenterWarn property */
    val isL2CenterWarn: Boolean,
    /** isL3Show property */
    val isL3Show: Boolean,
    /** l3StartX property */
    val l3StartX: Int,
    /** l3StartY property */
    val l3StartY: Int,
    /** l3EndX property */
    val l3EndX: Int,
    /** l3EndY property */
    val l3EndY: Int,
    /** l3MaxX property */
    val l3MaxX: Int,
    /** l3MaxY property */
    val l3MaxY: Int,
    /** l3MaxValue property */
    val l3MaxValue: Int,
    /** l3MinX property */
    val l3MinX: Int,
    /** l3MinY property */
    val l3MinY: Int,
    /** l3MinValue property */
    val l3MinValue: Int,
    /** l3AveValue property */
    val l3AveValue: Int,
    /** isL3MaxWarn property */
    val isL3MaxWarn: Boolean,
    /** isL3MinWarn property */
    val isL3MinWarn: Boolean,
    /** isL3CenterWarn property */
    val isL3CenterWarn: Boolean,

    /** isR1Show property */
    val isR1Show: Boolean,
    /** r1StartX property */
    val r1StartX: Int,
    /** r1StartY property */
    val r1StartY: Int,
    /** r1EndX property */
    val r1EndX: Int,
    /** r1EndY property */
    val r1EndY: Int,
    /** r1MaxX property */
    val r1MaxX: Int,
    /** r1MaxY property */
    val r1MaxY: Int,
    /** r1MaxValue property */
    val r1MaxValue: Int,
    /** r1MinX property */
    val r1MinX: Int,
    /** r1MinY property */
    val r1MinY: Int,
    /** r1MinValue property */
    val r1MinValue: Int,
    /** r1AveValue property */
    val r1AveValue: Int,
    /** isR1MaxWarn property */
    val isR1MaxWarn: Boolean,
    /** isR1MinWarn property */
    val isR1MinWarn: Boolean,
    /** isR1CenterWarn property */
    val isR1CenterWarn: Boolean,
    /** isR2Show property */
    val isR2Show: Boolean,
    /** r2StartX property */
    val r2StartX: Int,
    /** r2StartY property */
    val r2StartY: Int,
    /** r2EndX property */
    val r2EndX: Int,
    /** r2EndY property */
    val r2EndY: Int,
    /** r2MaxX property */
    val r2MaxX: Int,
    /** r2MaxY property */
    val r2MaxY: Int,
    /** r2MaxValue property */
    val r2MaxValue: Int,
    /** r2MinX property */
    val r2MinX: Int,
    /** r2MinY property */
    val r2MinY: Int,
    /** r2MinValue property */
    val r2MinValue: Int,
    /** r2AveValue property */
    val r2AveValue: Int,
    /** isR2MaxWarn property */
    val isR2MaxWarn: Boolean,
    /** isR2MinWarn property */
    val isR2MinWarn: Boolean,
    /** isR2CenterWarn property */
    val isR2CenterWarn: Boolean,
    /** isR3Show property */
    val isR3Show: Boolean,
    /** r3StartX property */
    val r3StartX: Int,
    /** r3StartY property */
    val r3StartY: Int,
    /** r3EndX property */
    val r3EndX: Int,
    /** r3EndY property */
    val r3EndY: Int,
    /** r3MaxX property */
    val r3MaxX: Int,
    /** r3MaxY property */
    val r3MaxY: Int,
    /** r3MaxValue property */
    val r3MaxValue: Int,
    /** r3MinX property */
    val r3MinX: Int,
    /** r3MinY property */
    val r3MinY: Int,
    /** r3MinValue property */
    val r3MinValue: Int,
    /** r3AveValue property */
    val r3AveValue: Int,
    /** isR3MaxWarn property */
    val isR3MaxWarn: Boolean,
    /** isR3MinWarn property */
    val isR3MinWarn: Boolean,
    /** isR3CenterWarn property */
    val isR3CenterWarn: Boolean,
) {

    constructor(byteArray: ByteArray) : this(
        isMaxShow = byteArray[0].toInt() and 0xff == 1,
        isMinShow = byteArray[1].toInt() and 0xff == 1,
        isCenterShow = byteArray[2].toInt() and 0xff == 1,
        maxX = (byteArray[4].toInt() and 0xff) or (byteArray[5].toInt() and 0xff shl 8),
        maxY = (byteArray[6].toInt() and 0xff) or (byteArray[7].toInt() and 0xff shl 8),
        maxValue = ((byteArray[8].toInt() and 0xff) or (byteArray[9].toInt() and 0xff shl 8)) - 2732,
        minX = (byteArray[10].toInt() and 0xff) or (byteArray[11].toInt() and 0xff shl 8),
        minY = (byteArray[12].toInt() and 0xff) or (byteArray[13].toInt() and 0xff shl 8),
        minValue = ((byteArray[14].toInt() and 0xff) or (byteArray[15].toInt() and 0xff shl 8)) - 2732,
        centerX = (byteArray[16].toInt() and 0xff) or (byteArray[17].toInt() and 0xff shl 8),
        centerY = (byteArray[18].toInt() and 0xff) or (byteArray[19].toInt() and 0xff shl 8),
        centerValue = ((byteArray[20].toInt() and 0xff) or (byteArray[21].toInt() and 0xff shl 8)) - 2732,
        isMaxWarn = byteArray[22].toInt() and 0xff == 1,
        isMinWarn = byteArray[23].toInt() and 0xff == 1,
        isCenterWarn = byteArray[24].toInt() and 0xff == 1,

        isP1Show = byteArray[26].toInt() and 0xff == 1,
        p1X = (byteArray[28].toInt() and 0xff) or (byteArray[29].toInt() and 0xff shl 8),
        p1Y = (byteArray[30].toInt() and 0xff) or (byteArray[31].toInt() and 0xff shl 8),
        p1Value = ((byteArray[32].toInt() and 0xff) or (byteArray[33].toInt() and 0xff shl 8)) - 2732,
        isP1MaxWarn = byteArray[34].toInt() and 0xff == 1,
        isP1MinWarn = byteArray[35].toInt() and 0xff == 1,
        isP1CenterWarn = byteArray[36].toInt() and 0xff == 1,
        isP2Show = byteArray[38].toInt() and 0xff == 1,
        p2X = (byteArray[40].toInt() and 0xff) or (byteArray[41].toInt() and 0xff shl 8),
        p2Y = (byteArray[42].toInt() and 0xff) or (byteArray[43].toInt() and 0xff shl 8),
        p2Value = ((byteArray[44].toInt() and 0xff) or (byteArray[45].toInt() and 0xff shl 8)) - 2732,
        isP2MaxWarn = byteArray[46].toInt() and 0xff == 1,
        isP2MinWarn = byteArray[47].toInt() and 0xff == 1,
        isP2CenterWarn = byteArray[48].toInt() and 0xff == 1,
        isP3Show = byteArray[50].toInt() and 0xff == 1,
        p3X = (byteArray[52].toInt() and 0xff) or (byteArray[53].toInt() and 0xff shl 8),
        p3Y = (byteArray[54].toInt() and 0xff) or (byteArray[55].toInt() and 0xff shl 8),
        p3Value = ((byteArray[56].toInt() and 0xff) or (byteArray[57].toInt() and 0xff shl 8)) - 2732,
        isP3MaxWarn = byteArray[58].toInt() and 0xff == 1,
        isP3MinWarn = byteArray[59].toInt() and 0xff == 1,
        isP3CenterWarn = byteArray[60].toInt() and 0xff == 1,

        isL1Show = byteArray[62].toInt() and 0xff == 1,
        l1StartX = (byteArray[64].toInt() and 0xff) or (byteArray[65].toInt() and 0xff shl 8),
        l1StartY = (byteArray[66].toInt() and 0xff) or (byteArray[67].toInt() and 0xff shl 8),
        l1EndX = (byteArray[68].toInt() and 0xff) or (byteArray[69].toInt() and 0xff shl 8),
        l1EndY = (byteArray[70].toInt() and 0xff) or (byteArray[71].toInt() and 0xff shl 8),
        l1MaxX = (byteArray[72].toInt() and 0xff) or (byteArray[73].toInt() and 0xff shl 8),
        l1MaxY = (byteArray[74].toInt() and 0xff) or (byteArray[75].toInt() and 0xff shl 8),
        l1MaxValue = ((byteArray[76].toInt() and 0xff) or (byteArray[77].toInt() and 0xff shl 8)) - 2732,
        l1MinX = (byteArray[78].toInt() and 0xff) or (byteArray[79].toInt() and 0xff shl 8),
        l1MinY = (byteArray[80].toInt() and 0xff) or (byteArray[81].toInt() and 0xff shl 8),
        l1MinValue = ((byteArray[82].toInt() and 0xff) or (byteArray[83].toInt() and 0xff shl 8)) - 2732,
        l1AveValue = ((byteArray[88].toInt() and 0xff) or (byteArray[89].toInt() and 0xff shl 8)) - 2732,
        isL1MaxWarn= byteArray[90].toInt() and 0xff == 1,
        isL1MinWarn= byteArray[91].toInt() and 0xff == 1,
        isL1CenterWarn= byteArray[92].toInt() and 0xff == 1,
        isL2Show = byteArray[94].toInt() and 0xff == 1,
        l2StartX = (byteArray[96].toInt() and 0xff) or (byteArray[97].toInt() and 0xff shl 8),
        l2StartY = (byteArray[98].toInt() and 0xff) or (byteArray[99].toInt() and 0xff shl 8),
        l2EndX = (byteArray[100].toInt() and 0xff) or (byteArray[101].toInt() and 0xff shl 8),
        l2EndY = (byteArray[102].toInt() and 0xff) or (byteArray[103].toInt() and 0xff shl 8),
        l2MaxX = (byteArray[104].toInt() and 0xff) or (byteArray[105].toInt() and 0xff shl 8),
        l2MaxY = (byteArray[106].toInt() and 0xff) or (byteArray[107].toInt() and 0xff shl 8),
        l2MaxValue = ((byteArray[108].toInt() and 0xff) or (byteArray[109].toInt() and 0xff shl 8)) - 2732,
        l2MinX = (byteArray[110].toInt() and 0xff) or (byteArray[111].toInt() and 0xff shl 8),
        l2MinY = (byteArray[112].toInt() and 0xff) or (byteArray[113].toInt() and 0xff shl 8),
        l2MinValue = ((byteArray[114].toInt() and 0xff) or (byteArray[115].toInt() and 0xff shl 8)) - 2732,
        l2AveValue = ((byteArray[120].toInt() and 0xff) or (byteArray[121].toInt() and 0xff shl 8)) - 2732,
        isL2MaxWarn= byteArray[122].toInt() and 0xff == 1,
        isL2MinWarn= byteArray[123].toInt() and 0xff == 1,
        isL2CenterWarn= byteArray[124].toInt() and 0xff == 1,
        isL3Show = byteArray[126].toInt() and 0xff == 1,
        l3StartX = (byteArray[128].toInt() and 0xff) or (byteArray[129].toInt() and 0xff shl 8),
        l3StartY = (byteArray[130].toInt() and 0xff) or (byteArray[131].toInt() and 0xff shl 8),
        l3EndX = (byteArray[132].toInt() and 0xff) or (byteArray[133].toInt() and 0xff shl 8),
        l3EndY = (byteArray[134].toInt() and 0xff) or (byteArray[135].toInt() and 0xff shl 8),
        l3MaxX = (byteArray[136].toInt() and 0xff) or (byteArray[137].toInt() and 0xff shl 8),
        l3MaxY = (byteArray[138].toInt() and 0xff) or (byteArray[139].toInt() and 0xff shl 8),
        l3MaxValue = ((byteArray[140].toInt() and 0xff) or (byteArray[141].toInt() and 0xff shl 8)) - 2732,
        l3MinX = (byteArray[142].toInt() and 0xff) or (byteArray[143].toInt() and 0xff shl 8),
        l3MinY = (byteArray[144].toInt() and 0xff) or (byteArray[145].toInt() and 0xff shl 8),
        l3MinValue = ((byteArray[146].toInt() and 0xff) or (byteArray[147].toInt() and 0xff shl 8)) - 2732,
        l3AveValue = ((byteArray[152].toInt() and 0xff) or (byteArray[153].toInt() and 0xff shl 8)) - 2732,
        isL3MaxWarn= byteArray[154].toInt() and 0xff == 1,
        isL3MinWarn= byteArray[155].toInt() and 0xff == 1,
        isL3CenterWarn= byteArray[156].toInt() and 0xff == 1,

        isR1Show = byteArray[158].toInt() and 0xff == 1,
        r1StartX = (byteArray[160].toInt() and 0xff) or (byteArray[161].toInt() and 0xff shl 8),
        r1StartY = (byteArray[162].toInt() and 0xff) or (byteArray[163].toInt() and 0xff shl 8),
        r1EndX = (byteArray[164].toInt() and 0xff) or (byteArray[165].toInt() and 0xff shl 8),
        r1EndY = (byteArray[166].toInt() and 0xff) or (byteArray[167].toInt() and 0xff shl 8),
        r1MaxX = (byteArray[168].toInt() and 0xff) or (byteArray[169].toInt() and 0xff shl 8),
        r1MaxY = (byteArray[170].toInt() and 0xff) or (byteArray[171].toInt() and 0xff shl 8),
        r1MaxValue = ((byteArray[172].toInt() and 0xff) or (byteArray[173].toInt() and 0xff shl 8)) - 2732,
        r1MinX = (byteArray[174].toInt() and 0xff) or (byteArray[175].toInt() and 0xff shl 8),
        r1MinY = (byteArray[176].toInt() and 0xff) or (byteArray[177].toInt() and 0xff shl 8),
        r1MinValue = ((byteArray[178].toInt() and 0xff) or (byteArray[179].toInt() and 0xff shl 8)) - 2732,
        r1AveValue = ((byteArray[184].toInt() and 0xff) or (byteArray[185].toInt() and 0xff shl 8)) - 2732,
        isR1MaxWarn= byteArray[186].toInt() and 0xff == 1,
        isR1MinWarn= byteArray[187].toInt() and 0xff == 1,
        isR1CenterWarn= byteArray[188].toInt() and 0xff == 1,
        isR2Show = byteArray[190].toInt() and 0xff == 1,
        r2StartX = (byteArray[192].toInt() and 0xff) or (byteArray[193].toInt() and 0xff shl 8),
        r2StartY = (byteArray[194].toInt() and 0xff) or (byteArray[195].toInt() and 0xff shl 8),
        r2EndX = (byteArray[196].toInt() and 0xff) or (byteArray[197].toInt() and 0xff shl 8),
        r2EndY = (byteArray[198].toInt() and 0xff) or (byteArray[199].toInt() and 0xff shl 8),
        r2MaxX = (byteArray[200].toInt() and 0xff) or (byteArray[201].toInt() and 0xff shl 8),
        r2MaxY = (byteArray[202].toInt() and 0xff) or (byteArray[203].toInt() and 0xff shl 8),
        r2MaxValue = ((byteArray[204].toInt() and 0xff) or (byteArray[205].toInt() and 0xff shl 8)) - 2732,
        r2MinX = (byteArray[206].toInt() and 0xff) or (byteArray[207].toInt() and 0xff shl 8),
        r2MinY = (byteArray[208].toInt() and 0xff) or (byteArray[209].toInt() and 0xff shl 8),
        r2MinValue = ((byteArray[210].toInt() and 0xff) or (byteArray[211].toInt() and 0xff shl 8)) - 2732,
        r2AveValue = ((byteArray[216].toInt() and 0xff) or (byteArray[217].toInt() and 0xff shl 8)) - 2732,
        isR2MaxWarn= byteArray[218].toInt() and 0xff == 1,
        isR2MinWarn= byteArray[219].toInt() and 0xff == 1,
        isR2CenterWarn= byteArray[220].toInt() and 0xff == 1,
        isR3Show = byteArray[222].toInt() and 0xff == 1,
        r3StartX = (byteArray[224].toInt() and 0xff) or (byteArray[225].toInt() and 0xff shl 8),
        r3StartY = (byteArray[226].toInt() and 0xff) or (byteArray[227].toInt() and 0xff shl 8),
        r3EndX = (byteArray[228].toInt() and 0xff) or (byteArray[229].toInt() and 0xff shl 8),
        r3EndY = (byteArray[230].toInt() and 0xff) or (byteArray[231].toInt() and 0xff shl 8),
        r3MaxX = (byteArray[232].toInt() and 0xff) or (byteArray[233].toInt() and 0xff shl 8),
        r3MaxY = (byteArray[234].toInt() and 0xff) or (byteArray[235].toInt() and 0xff shl 8),
        r3MaxValue = ((byteArray[236].toInt() and 0xff) or (byteArray[237].toInt() and 0xff shl 8)) - 2732,
        r3MinX = (byteArray[238].toInt() and 0xff) or (byteArray[239].toInt() and 0xff shl 8),
        r3MinY = (byteArray[240].toInt() and 0xff) or (byteArray[241].toInt() and 0xff shl 8),
        r3MinValue = ((byteArray[242].toInt() and 0xff) or (byteArray[243].toInt() and 0xff shl 8)) - 2732,
        r3AveValue = ((byteArray[248].toInt() and 0xff) or (byteArray[249].toInt() and 0xff shl 8)) - 2732,
        isR3MaxWarn= byteArray[250].toInt() and 0xff == 1,
        isR3MinWarn= byteArray[251].toInt() and 0xff == 1,
        isR3CenterWarn= byteArray[252].toInt() and 0xff == 1,
    )

    companion object {
        private fun Boolean.openText(): String = if (this) "" else ""

        private fun Int.toCStr(): String = "${this / 10}${if (this % 10 == 0) "" else ".${this % 10}"}°C"
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        if (isMaxShow) {
            stringBuilder.append(" ($maxX, $maxY) ${maxValue.toCStr()} ${isMaxWarn.openText()}\n")
        }
        if (isMinShow) {
            stringBuilder.append(" ($minX, $minY) ${minValue.toCStr()} ${isMinWarn.openText()}\n")
        }
        if (isCenterShow) {
            stringBuilder.append(" ($centerX, $centerY) ${centerValue.toCStr()} ${isCenterWarn.openText()}\n")
        }

        if (isP1Show) {
            stringBuilder.append("1 ($p1X, $p1Y) ${p1Value.toCStr()}\n")
        }
        if (isP2Show) {
            stringBuilder.append("2 ($p2X, $p2Y) ${p2Value.toCStr()}\n")
        }
        if (isP3Show) {
            stringBuilder.append("3 ($p3X, $p3Y) ${p3Value.toCStr()}\n")
        }

        if (isL1Show) {
            stringBuilder.append("1 ($l1StartX, $l1StartY)-($l1EndX, $l1EndY) ")
            stringBuilder.append("${l1MinValue.toCStr()}($l1MinX, $l1MinY) ${l1MaxValue.toCStr()}($l1MaxX, $l1MaxY) ")
            stringBuilder.append("${l1AveValue.toCStr()}\n")
        }
        if (isL2Show) {
            stringBuilder.append("2 ($l2StartX, $l2StartY)-($l2EndX, $l2EndY) ")
            stringBuilder.append("${l2MinValue.toCStr()}($l2MinX, $l2MinY) ${l2MaxValue.toCStr()}($l2MaxX, $l2MaxY) ")
            stringBuilder.append("${l2AveValue.toCStr()}\n")
        }
        if (isL3Show) {
            stringBuilder.append("3 ($l3StartX, $l3StartY)-($l3EndX, $l3EndY) ")
            stringBuilder.append("${l3MinValue.toCStr()}($l3MinX, $l3MinY) ${l3MaxValue.toCStr()}($l3MaxX, $l3MaxY) ")
            stringBuilder.append("${l3AveValue.toCStr()}\n")
        }

        if (isR1Show) {
            stringBuilder.append("1 ($r1StartX, $r1StartY)-($r1EndX, $r1EndY) ")
            stringBuilder.append("${r1MinValue.toCStr()}($r1MinX, $r1MinY) ${r1MaxValue.toCStr()}($r1MaxX, $r1MaxY) ")
            stringBuilder.append("${r1AveValue.toCStr()}\n")
        }
        if (isR2Show) {
            stringBuilder.append("2 ($r2StartX, $r2StartY)-($r2EndX, $r2EndY) ")
            stringBuilder.append("${r2MinValue.toCStr()}($r2MinX, $r2MinY) ${r2MaxValue.toCStr()}($r2MaxX, $r2MaxY) ")
            stringBuilder.append("${l2AveValue.toCStr()}\n")
        }
        if (isR3Show) {
            stringBuilder.append("3 ($r3StartX, $r3StartY)-($r3EndX, $r3EndY) ")
            stringBuilder.append("${r3MinValue.toCStr()}($r3MinX, $r3MinY) ${r3MaxValue.toCStr()}($r3MaxX, $r3MaxY) ")
            stringBuilder.append("${r3AveValue.toCStr()}\n")
        }
        return stringBuilder.toString()
    }
}
