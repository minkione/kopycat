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
package ru.inforion.lab403.kopycat.cores.mips.instructions.cpu.memory

import ru.inforion.lab403.common.extensions.bext
import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.mips.instructions.RtOffsetInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.GPR
import ru.inforion.lab403.kopycat.cores.mips.operands.MipsDisplacement
import ru.inforion.lab403.kopycat.modules.cores.MipsCore

/**
 *
 * SWL rt, offset(base)
 */
class swl(core: MipsCore,
          data: Long,
          rt: GPR,
          off: MipsDisplacement) : RtOffsetInsn(core, data, Type.VOID, rt, off) {

    override val mnem = "swl"

    override fun execute() {
        // I hate mips...
        val dataword = vrt

        val vAddr = address

        val alignAddr = vAddr and 0xFFFFFFFC
        val byte = (vAddr[1..0] xor core.cpu.bigEndianCPU.bext(2)).toInt()

        val memword = core.inl(alignAddr)

        val hi = memword[31..8 * (byte + 1)]
        val lo = dataword[31..24 - 8 * byte]

        val result = hi.shl(8 * (byte + 1)) or lo

        core.outl(alignAddr, result)
    }
}
