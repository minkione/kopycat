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
package ru.inforion.lab403.kopycat.cores.arm.instructions.cpu.unconditional

import ru.inforion.lab403.common.extensions.clr
import ru.inforion.lab403.common.extensions.insert
import ru.inforion.lab403.common.extensions.set
import ru.inforion.lab403.kopycat.cores.arm.enums.Condition
import ru.inforion.lab403.kopycat.cores.arm.exceptions.ARMHardwareException
import ru.inforion.lab403.kopycat.cores.arm.instructions.AARMInstruction
import ru.inforion.lab403.kopycat.cores.arm.operands.ARMRegister
import ru.inforion.lab403.kopycat.modules.cores.AARMCore
import ru.inforion.lab403.kopycat.modules.cores.AARMv6Core



// See A8.8.126
class PLD(val cpu: AARMCore,
          opcode: Long,
          cond: Condition,
          val rn: ARMRegister,
          val imm32: Long,
          val add: Boolean,
          val is_pldw: Boolean):
        AARMInstruction(cpu, Type.VOID, cond, opcode) {
    override val mnem = "PLD"

    override fun execute() {
        // TODO: data preload isn't implemented
//        val address = if (add) (rn.value(core) + imm32) else (rn.value(core) - imm32)
//        if (is_pldw)
//            Hint_PreloadDataForWrite(address)
//        else
//            Hint_PreloadData(address)
    }
}