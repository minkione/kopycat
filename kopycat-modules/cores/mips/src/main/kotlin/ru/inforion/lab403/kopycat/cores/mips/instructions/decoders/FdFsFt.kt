/*
 *
 * This file is part of Kopycat emulator software.
 *
 * Copyright (C) 2020 INFORION, LLC
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * Non-free licenses may also be purchased from INFORION, LLC, 
 * for users who do not want their programs protected by the GPL. 
 * Contact us for details kopycat@inforion.ru
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package ru.inforion.lab403.kopycat.cores.mips.instructions.decoders

import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.mips.instructions.AMipsInstruction
import ru.inforion.lab403.kopycat.cores.mips.operands.FPR
import ru.inforion.lab403.kopycat.modules.cores.MipsCore


class FdFsFt(
        core: MipsCore,
        val construct: (MipsCore, Long, FPR, FPR, FPR) -> AMipsInstruction
) : ADecoder(core) {

//    val fpu:FPU get() = dev.fpu
//
//    var fd: Int by IntOperandField(1)
//    var fs: Int by IntOperandField(2)
//    var ft: Int by IntOperandField(3)
//
//    var dfd : Long by DoubleRegister(1)
//    var dfs : Long by DoubleRegister(2)
//    var dft : Long by DoubleRegister(3)

    override fun decode(data: Long): AMipsInstruction {
        val fd = data[10..6].toInt()
        val fs = data[15..11].toInt()
        val ft = data[20..16].toInt()
        return construct(core, data,
                FPR(fd),
                FPR(fs),
                FPR(ft))
    }
}
