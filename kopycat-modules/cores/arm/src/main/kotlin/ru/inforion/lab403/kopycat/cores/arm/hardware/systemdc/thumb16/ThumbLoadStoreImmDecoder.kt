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
package ru.inforion.lab403.kopycat.cores.arm.hardware.systemdc.thumb16

import ru.inforion.lab403.common.extensions.asInt
import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.arm.enums.Condition
import ru.inforion.lab403.kopycat.cores.arm.enums.Condition.UN
import ru.inforion.lab403.kopycat.cores.arm.hardware.systemdc.decoders.ADecoder
import ru.inforion.lab403.kopycat.cores.arm.instructions.AARMInstruction
import ru.inforion.lab403.kopycat.cores.arm.operands.ARMImmediate
import ru.inforion.lab403.kopycat.cores.arm.hardware.registers.GPRBank
import ru.inforion.lab403.kopycat.cores.arm.operands.ARMRegister
import ru.inforion.lab403.kopycat.cores.base.operands.Immediate
import ru.inforion.lab403.kopycat.modules.cores.AARMCore

object ThumbLoadStoreImmDecoder{
    class T1(cpu: AARMCore,
             private val constructor: (
                     cpu: AARMCore,
                     opcode: Long,
                     cond: Condition,
                     index: Boolean,
                     add: Boolean,
                     wback: Boolean,
                     rn: ARMRegister,
                     rt: ARMRegister,
                     imm: Immediate<AARMCore>,
                     size: Int) -> AARMInstruction) : ADecoder<AARMInstruction>(cpu) {
        override fun decode(data: Long): AARMInstruction {
            val rt = GPRBank.Operand(data[2..0].asInt)
            val rn = GPRBank.Operand(data[5..3].asInt)
            val imm = ARMImmediate(data[10..6] shl 2, true)
            return constructor(core, data, UN, true, true, false, rn, rt, imm, 2)
        }
    }
    class T2(cpu: AARMCore,
             private val constructor: (
                     cpu: AARMCore,
                     opcode: Long,
                     cond: Condition,
                     index: Boolean,
                     add: Boolean,
                     wback: Boolean,
                     rn: ARMRegister,
                     rt: ARMRegister,
                     imm: Immediate<AARMCore>,
                     size: Int) -> AARMInstruction) : ADecoder<AARMInstruction>(cpu) {
        override fun decode(data: Long): AARMInstruction {
            val rt = GPRBank.Operand(data[10..8].asInt)
            val rn = GPRBank.Operand(13)
            val imm = ARMImmediate(data[7..0] shl 2, true)
            return constructor(core, data, UN, true, true, false, rn, rt, imm, 2)
        }
    }
}