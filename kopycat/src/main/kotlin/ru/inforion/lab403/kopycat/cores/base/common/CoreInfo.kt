package ru.inforion.lab403.kopycat.cores.base.common

import ru.inforion.lab403.common.extensions.hex
import ru.inforion.lab403.common.extensions.hex8
import ru.inforion.lab403.common.extensions.stretch
import ru.inforion.lab403.common.logging.logger
import ru.inforion.lab403.common.proposal.DynamicClassLoader
import ru.inforion.lab403.kopycat.auxiliary.LimitedQueue
import ru.inforion.lab403.kopycat.cores.base.AGenericCore
import ru.inforion.lab403.kopycat.interfaces.IResettable
import java.io.InputStream
import java.util.*

class CoreInfo<C: AGenericCore>(val core: C): IResettable {
    companion object {
        val log = logger()
    }

    private data class StackElement(val pc: Long, val ra: Long)

    private val trace = LimitedQueue<Long>(32)
    private val stacktrace = Stack<StackElement>()
    private var translate: (address: Long) -> String = { it.hex8 }

    /**
     * {EN}
     * Saved program counter before ```cpu.execute()``` using [saveProgramCounter] function
     * in [ACore.doExecuteInstruction]. Required to save PC if exception will occurred
     * {EN}
     */
    private var lastPC = 0L

    var totalExecuted = 0L
        private set

    private enum class MapDataState {
        Begin,
        Segments,
        Symbols,
        End
    }

    private data class Symbol(val address: Long, val name: String)

    private fun mapTranslate(symbols: MutableList<Symbol>, address: Long): String {
        val (location, _) = symbols.zipWithNext().find { (_, next) -> next.address > address } ?: return address.hex8
        val offset = address - location.address
        return "[${address.hex8}] ${location.name}+0x${offset.hex}"
    }

    fun loadGCCMapFile(stream: InputStream) {

        val mapping = mutableListOf<Symbol>()

        stream.bufferedReader()
                .readLines()
                .filter { it.isNotEmpty() }
                .forEach { line ->
                    val data = line.trim().split(' ')
                    val address = data[0].toLong(16)
                    val name = data.last()
                    mapping.add(Symbol(address, name))
                }

        translate = { address -> mapTranslate(mapping, address) }
    }

    fun loadIDAMapFile(stream: InputStream) {
        var state = MapDataState.Begin

        val segments = mutableMapOf<Long, Long>()
        val mapping = mutableListOf<Symbol>()

        stream.bufferedReader()
                .readLines()
                .filter { it.isNotEmpty() }
                .forEach { line ->
                    when (state) {
                        MapDataState.Begin -> {
                            if (line.trim().startsWith("Start"))
                                state = MapDataState.Segments
                        }
                        MapDataState.Segments -> {
                            if (line.trim().startsWith("Address")) {
                                state = MapDataState.Symbols
                            } else {
                                val data = line.trim().split(' ', ':').filter { it.isNotEmpty() }
                                val secInd = data[0].toLong(16)
                                val secStart = data[1].toLong(16)
                                segments[secInd] = secStart
                            }
                        }
                        MapDataState.Symbols -> {
                            if (line.trim().startsWith("Program")) {
                                state = MapDataState.End
                            } else {
                                val data = line.trim().split(' ', ':').filter { it.isNotEmpty() }
                                val secInd = segments[data[0].toLong(16)]
                                if (secInd != null) {
                                    val address = secInd + data[1].toLong(16)
                                    val name = data.last()
                                    mapping.add(Symbol(address, name))
                                }
                            }
                        }
                        MapDataState.End -> {

                        }
                    }
                }

        translate = { address -> mapTranslate(mapping, address) }
    }

    private fun doProgramCounterTrace(exception: Boolean) {
        if (!exception)
            trace.add(core.pc)
    }

    private fun doStackTrace(exception: Boolean) {
        if (core.pc != lastPC + core.cpu.insn.size) { // Jump occurred
            if (exception || core.cpu.callOccurred) { // Sub call
                stacktrace.push(StackElement(lastPC, lastPC + core.cpu.insn.size))
            } else if (stacktrace.isNotEmpty() && stacktrace.peek().ra == core.pc) { // Return from call
                stacktrace.pop()
            }
        }
    }

    fun printTrace() {
        log.info { "-------------------- Program counter trace: --------------------" }
        trace.forEach { log.info { translate(it) } }
        log.info { translate(core.pc) }
    }

    fun printStackTrace() {
        log.info { "-------------------- Stack trace: ------------------------------" }
        stacktrace.forEach { log.info { translate(it.pc) } }
    }

    fun printCpuState() {
        val regsOnLine = 4
        val buffer = StringBuffer()
        log.info { "-------------------- CPU state: --------------------------------" }

        log.info { "Total executed instructions: $totalExecuted" }

        repeat(core.cpu.count()) { reg ->
            val name = "R$reg".stretch(3)
            val value = "0x${core.reg(reg).hex8}"
            buffer.append("$name = $value".stretch(20))

            // print by 4 regs on line
            if ((reg + 1) % regsOnLine == 0) {
                val line = buffer.toString()
                buffer.setLength(0)
                log.info { line }
            }
        }
        if (buffer.isNotBlank()) {
            log.info { buffer.toString() }
        }
    }

    fun dump(cpu: Boolean = true, stack: Boolean = true, trace: Boolean = true) {
        if (cpu) printCpuState()
        if (stack) printStackTrace()
        if (trace) printTrace()
    }

    fun saveProgramCounter() {
        lastPC = core.pc
    }

    fun trace(exception: Boolean) {
        totalExecuted++
        doProgramCounterTrace(exception)
        doStackTrace(exception)
    }

    override fun reset() {
        lastPC = 0
        totalExecuted = 0
        trace.clear()
        stacktrace.clear()
    }
}