import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public final class Constants {

    public static final long ELF_MAGIC = 0x464C457F;
    public static long[] bytes;
    public static long TAGINDEX = 0;
    public static long TOTAL;
    public static Map<String, SectionHeader> sections = new HashMap<>();
    public static Map<Long, String> tags = new HashMap<Long, String>();
    public static List<Symbol> symbols = new ArrayList<>();
    public static final Map<Pair<Integer, Integer>, String> FmtRM = Map.ofEntries(
            entry(new Pair<>(0, 0), "add"),
            entry(new Pair<>(0,0x20), "sub"),
            entry(new Pair<>(0x4,0), "xor"),
            entry(new Pair<>(0x6,0), "or"),
            entry(new Pair<>(0x7,0), "and"),
            entry(new Pair<>(0x1,0), "sli"),
            entry(new Pair<>(0x5,0), "srl"),
            entry(new Pair<>(0x5,0x20), "sra"),
            entry(new Pair<>(0x2,0), "slt"),
            entry(new Pair<>(0x3,0), "sltu"),
            entry(new Pair<>(0x0,0x1), "mul"),
            entry(new Pair<>(0x1,0x1), "mulh"),
            entry(new Pair<>(0x2,0x1), "mulsu"),
            entry(new Pair<>(0x3,0x1), "mulu"),
            entry(new Pair<>(0x4,0x1), "div"),
            entry(new Pair<>(0x5,0x1), "divu"),
            entry(new Pair<>(0x6,0x1), "rem"),
            entry(new Pair<>(0x7,0x1), "remu")
    );
    public static final Map<Pair<Integer, Integer>, String> FmtI = Map.ofEntries(
            entry(new Pair<>(0, 0), "addi"),
            entry(new Pair<>(0x4, 0), "xori"),
            entry(new Pair<>(0x6, 0), "ori"),
            entry(new Pair<>(0x7, 0), "0"),
            entry(new Pair<>(0x1, 0), "slli"),
            entry(new Pair<>(0x5, 0), "srli"),
            entry(new Pair<>(0x5, 0x20), "srai"),
            entry(new Pair<>(0x2, 0), "slti"),
            entry(new Pair<>(0x3, 0), "sltiu")
    );
    public static final Map<Pair<Integer, Integer>, String> FmtILoad = Map.ofEntries(
            entry(new Pair<>(0, 0), "lb"),
            entry(new Pair<>(0x1, 0), "lh"),
            entry(new Pair<>(0x2, 0), "lw"),
            entry(new Pair<>(0x4, 0), "lbu"),
            entry(new Pair<>(0x5, 0), "lhu")
    );
    public static final Map<Pair<Integer, Integer>, String> FmtS = Map.ofEntries(
            entry(new Pair<>(0, 0), "sb"),
            entry(new Pair<>(0x1, 0), "sh"),
            entry(new Pair<>(0x2, 0), "sw")
    );
    public static final Map<Pair<Integer, Integer>, String> FmtB = Map.ofEntries(
            entry(new Pair<>(0, 0), "beq"),
            entry(new Pair<>(0x1, 0), "bne"),
            entry(new Pair<>(0x4, 0), "blt"),
            entry(new Pair<>(0x5, 0), "bge"),
            entry(new Pair<>(0x6, 0), "bltu"),
            entry(new Pair<>(0x7, 0), "bgeu")
    );
    public static final Map<Integer, String> registerNames = Map.ofEntries(
            entry(0, "zero"),
            entry(1, "ra"),
            entry(2, "sp"),
            entry(3, "gp"),
            entry(4, "tp"),
            entry(5, "t0"),
            entry(6, "t1"),
            entry(7, "t2"),
            entry(8, "s0 / fp"),
            entry(9, "s1"),
            entry(10, "a0"),
            entry(11, "a1"),
            entry(12, "a2"),
            entry(13, "a3"),
            entry(14, "a4"),
            entry(15, "a5"),
            entry(16, "a6"),
            entry(17, "a7"),
            entry(18, "s2"),
            entry(19, "s3"),
            entry(20, "s4"),
            entry(21, "s5"),
            entry(22, "s6"),
            entry(23, "s7"),
            entry(24, "s8"),
            entry(25, "s9"),
            entry(26, "s10"),
            entry(27, "s11"),
            entry(28, "t3"),
            entry(29, "t4"),
            entry(30, "t5"),
            entry(31, "t6")
    );
    public enum FMT{
        RM,
        I,
        ILoad,
        S,
        B,
        J,
        IJalr,
        ULui,
        UAui,
        IEnv,
        unknown_instruction
    };

    public static long extractBytes(long offset, long byteNum) throws Exception {
        if (byteNum <= 0 || byteNum > 4) {
            throw new Exception("Wrong number of bytes to read in extractBytes()");
        }
        long result = 0;
        for(long i = 0; i < byteNum; i++){
            if (offset + byteNum - i - 1 < 0 || offset + byteNum - i - 1 >= TOTAL) {
                throw new Exception("Index out of boundaries when trying to read in extractBytes()");
            }
            result = result * 256 + (bytes[(int) (offset + byteNum - i - 1)]);
        }
        return result;
    }
    public static long cutInstruction(long bin, long from, long to) {
        bin = bin & ((1L << (from + 1)) - 1);
        bin = bin & (-(1L << to));
        return (bin >> to);
    }
    public static long immToSigned(long n, long where){
        if ((n & (1L << (where - 1))) != 0) {
            return (n | -(1L << where));
        }
        else {
            return n;
        }
    }
    private Constants(){

    }
}
