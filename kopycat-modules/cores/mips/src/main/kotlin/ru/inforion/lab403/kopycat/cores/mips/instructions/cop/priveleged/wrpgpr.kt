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
package ru.inforion.lab403.kopycat.cores.mips.instructions.cop.priveleged

import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.base.abstracts.AInstruction.Type.VOID
import ru.inforion.lab403.kopycat.cores.mips.instructions.RdRtInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.GPR
import ru.inforion.lab403.kopycat.modules.cores.MipsCore

/**
 *
 * WRPGPR rd, rt
 */
class wrpgpr(
        core: MipsCore,
        data: Long,
        rd: GPR,
        rt: GPR
) : RdRtInsn(core, data, VOID, rd, rt) {

    override val mnem = "wrpgpr"

    override fun execute() {
        val pss = core.cop.regs.SRSCtl[9..6].toInt()
        val shadow = core.cpu.sgprs[pss]
        // FIXME: Remove write intern here rd.value should used
        shadow.writeIntern(rd.reg, vrt)
    }
}
