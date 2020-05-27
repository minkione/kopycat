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
package ru.inforion.lab403.kopycat.cores.arm.instructions.cpu.rload

import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.arm.enums.Condition
import ru.inforion.lab403.kopycat.cores.arm.exceptions.ARMHardwareException.Unknown
import ru.inforion.lab403.kopycat.cores.arm.instructions.AARMInstruction
import ru.inforion.lab403.kopycat.cores.arm.operands.ARMRegister
import ru.inforion.lab403.kopycat.cores.arm.operands.ARMRegisterList
import ru.inforion.lab403.kopycat.cores.base.enums.Datatype
import ru.inforion.lab403.kopycat.cores.base.like
import ru.inforion.lab403.kopycat.modules.cores.AARMCore



class LDMDB(core: AARMCore,
            opcode: Long,
            cond: Condition,
            val wback: Boolean,
            val rn: ARMRegister,
            val registers: ARMRegisterList,
            size: Int):
        AARMInstruction(core, Type.VOID, cond, opcode, rn, registers, size = size) {
    override val mnem = "LDMDB$mcnd"

    override fun execute() {
        var address = rn.value(core) - 4 * registers.bitCount
        // There is difference from datasheet (all registers save in common loop) -> no LoadWritePC called
        registers.forEachIndexed { i, reg ->
            if (reg.reg == core.cpu.regs.pc.reg) {
                core.cpu.LoadWritePC(core.inl(address like Datatype.DWORD))
            }
            else {
                reg.value(core, core.inl(address like Datatype.DWORD))
            }
            address += 4
        }
        if(wback) {
            if (registers.rbits[rn.reg] == 0L) rn.value(core, rn.value(core) - 4 * registers.bitCount)
            else throw Unknown
        }
    }
}