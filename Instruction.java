import java.util.Objects;

public class Instruction{
    public String name;
    public String rd;
    public String rs1;
    public String rs2;
    public long imm;
    public Constants.FMT type;
    public long bin;
    public Instruction(long offset, long address) throws Exception {
        bin = Constants.extractBytes(offset, 4);
        long opcode = Constants.cutInstruction(bin, 6, 0);
        name = "unknown instruction";
        type = Constants.FMT.unknown_instruction;

        if(opcode == 0b0110011){
            type = Constants.FMT.RM;
            long rdId = Constants.cutInstruction(bin, 11, 7);
            long funct3 = Constants.cutInstruction(bin, 14, 12);
            long funct7 = Constants.cutInstruction(bin, 31, 25);
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            long rs2Id = Constants.cutInstruction(bin, 24, 20);
            name = Constants.FmtRM.get(new Pair<>((int)funct3, (int)funct7));
            imm = 0;
            rd = Constants.registerNames.get((int)rdId);
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = Constants.registerNames.get((int)rs2Id);
        }
        if(opcode == 0b0010011) {
            type = Constants.FMT.I;
            long rdId = Constants.cutInstruction(bin, 11, 7);
            long funct3 = Constants.cutInstruction(bin, 14, 12);
            long funct7 = 0;
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            imm = Constants.cutInstruction(bin, 31, 20);
            imm = Constants.immToSigned(imm, 12);


            if(funct3 == 0x5) {
                funct7 = Constants.cutInstruction(bin, 31, 25);
            }
            name = Constants.FmtI.get(new Pair<>((int)funct3, (int)funct7));
            rd = Constants.registerNames.get((int)rdId);
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = "";
        }
        if(opcode == 0b0000011) {
            type = Constants.FMT.ILoad;
            long rdId = Constants.cutInstruction(bin, 11, 7);
            long funct3 = Constants.cutInstruction(bin, 14, 12);
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            imm = Constants.immToSigned(Constants.cutInstruction(bin, 31, 20), 12);
            name = Constants.FmtILoad.get(new Pair<>((int)funct3, 0));
            rd = Constants.registerNames.get((int)rdId);
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = "";
        }
        if(opcode == 0b1100111) {
            type = Constants.FMT.IJalr;
            long rdId = Constants.cutInstruction(bin, 11, 7);
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            imm = Constants.immToSigned(Constants.cutInstruction(bin, 31, 20), 12);

            name = "jalr";
            rd = Constants.registerNames.get((int)rdId);
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = "";
        }
        if(opcode == 0b1110011) {
            type = Constants.FMT.IEnv;
            long rdId = Constants.cutInstruction(bin, 11, 7);
            long funct7 = Constants.cutInstruction(bin, 31, 20);
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            imm = Constants.immToSigned(Constants.cutInstruction(bin, 31, 20), 12);

            name = (funct7 == 0 ? "ecall" : "ebreak");
            rd = Constants.registerNames.get((int)rdId);
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = "";
        }
        if(opcode == 0b0100011) {
            type = Constants.FMT.S;
            imm = 0;
            imm |= (Constants.cutInstruction(bin, 31, 25) >> 5);
            imm |= Constants.cutInstruction(bin, 11, 7);


            long funct3 = Constants.cutInstruction(bin, 14, 12);
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            long rs2Id = Constants.cutInstruction(bin, 24, 20);
            name = Constants.FmtS.get(new Pair<>((int)funct3, 0));
            imm = Constants.immToSigned(imm, 12);
            rd = "";
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = Constants.registerNames.get((int)rs2Id);
        }
        if(opcode == 0b1100011) {
            type = Constants.FMT.B;
            long imm12_105 = Constants.cutInstruction(bin, 31, 25);
            long imm41_11 = Constants.cutInstruction(bin, 11, 7);

            long imm12th = Constants.cutInstruction(imm12_105, 6, 6);
            long imm105 = Constants.cutInstruction(imm12_105, 5, 0);
            long imm41 = Constants.cutInstruction(imm41_11, 4, 1);
            long imm11th = Constants.cutInstruction(imm41_11, 0, 0);
            imm = 0;
            imm |= (imm12th << 12);
            imm |= (imm11th << 11);
            imm |= (imm105 << 5);
            imm |= (imm41 << 1);


            long funct3 = Constants.cutInstruction(bin, 14, 12);
            long rs1Id = Constants.cutInstruction(bin, 19, 15);
            long rs2Id = Constants.cutInstruction(bin, 24, 20);

            name = Constants.FmtB.get(new Pair<>((int)funct3, 0));
            imm = Constants.immToSigned(imm, 13);
            if(!Constants.tags.containsKey(address + imm)){
                String nextTag = "<L" + Constants.TAGINDEX + ">";
                Constants.tags.put(address + imm, nextTag);
                Constants.TAGINDEX++;
            }
            rd = "";
            rs1 = Constants.registerNames.get((int)rs1Id);
            rs2 = Constants.registerNames.get((int)rs2Id);
        }
        if(opcode == 0b1101111) {
            type = Constants.FMT.J;
            imm = 0;
            long imm20th = Constants.cutInstruction(bin, 31, 31);
            long imm101 = Constants.cutInstruction(bin, 30, 21);
            long imm11th = Constants.cutInstruction(bin, 20, 20);
            long imm1912 = Constants.cutInstruction(bin, 19, 12);

            imm |= (imm20th << 20);
            imm |= (imm101 << 1);
            imm |= (imm11th << 11);
            imm |= (imm1912 << 12);

            imm = Constants.immToSigned(imm, 21);

            long rdId = Constants.cutInstruction(bin, 11, 7);
            name = "jal";
            if(!Constants.tags.containsKey(address + imm)) {
                String nextTag = "<L" + Constants.TAGINDEX + ">";
                Constants.tags.put(address + imm, nextTag);
                Constants.TAGINDEX++;
            }
            imm = Constants.immToSigned(imm, 21);
            rd = Constants.registerNames.get((int)rdId);
            rs1 = "";
            rs2 = "";
        }
        if(opcode == 0b0110111) {
            type = Constants.FMT.ULui;
            name = "lui";
            imm = Constants.immToSigned(Constants.cutInstruction(bin, 31, 12), 21);
            long rdId = Constants.cutInstruction(bin, 11, 7);
            rd = Constants.registerNames.get((int)rdId);
            rs1 = "";
            rs2 = "";
        }
        if(opcode == 0b0010111) {
            type = Constants.FMT.UAui;
            name = "auipc";
            imm = Constants.immToSigned(Constants.cutInstruction(bin, 31, 12), 21);
            long rdId = Constants.cutInstruction(bin, 11, 7);
            rd = Constants.registerNames.get((int)rdId);
            rs1 = "";
            rs2 = "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instruction that = (Instruction) o;
        return imm == that.imm && bin == that.bin && Objects.equals(name, that.name) && Objects.equals(rd, that.rd) && Objects.equals(rs1, that.rs1) && Objects.equals(rs2, that.rs2) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rd, rs1, rs2, imm, type, bin);
    }
}