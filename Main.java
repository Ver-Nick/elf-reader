import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length < 3){
            throw new Exception("Not enough arguments");
        }
        String input_file = args[1];
        String output_file = args[2];

        File f1 = new File(input_file);
        if(!f1.exists() || f1.isDirectory()) {
            System.out.println("Input file not found");
            return;
        }

        File f2 = new File(output_file);
        if(!f2.exists() || f2.isDirectory()) {
            System.out.println("Output file not found");
            return;
        }

        byte[] bytes_tmp;
        try (DataInputStream reader = new DataInputStream(new FileInputStream(input_file))) {
            bytes_tmp = reader.readAllBytes();
        } catch(Exception ex) {
            throw new Exception("Error reading file: " + ex.getMessage());
        }

        Constants.TOTAL = bytes_tmp.length;
        Constants.bytes = new long[(int) Constants.TOTAL];
        for(long i = 0; i < Constants.TOTAL; i++) {
            long c = ((bytes_tmp[(int) i] < 0) ? 256 : 0) + bytes_tmp[(int) i];
            Constants.bytes[(int) i] = c;
        }

        long magic = Constants.extractBytes(0, 4);
        if(magic != Constants.ELF_MAGIC) {
            throw new Exception("Not an ELF file");
        }
        // Extract elf header fields
        Header header = new Header();

        if(header.e_machine != 0xF3) {
            throw new Exception("File is not for RISC-V architecture");
        }

        if(header.e_version != 1) {
            throw new Exception("Wrong file version");
        }

        if(header.e_ehsize != 52) {
            throw new Exception("Wrong header size");
        }

        // Parse section headers and their names
        SectionHeader sectionTable = new SectionHeader(header.e_shoff, header.e_shstrndx, header.e_shentsize);
        for(long i = 0; i < header.e_shnum; i++){
            SectionHeader sectionHeader = new SectionHeader(header.e_shoff, i, header.e_shentsize);

            long pos = sectionTable.sh_offset + sectionHeader.sh_name;

            StringBuilder name = new StringBuilder();
            while(Constants.bytes[(int) pos] != 0){
                name.append((char) Constants.bytes[(int) pos]);
                pos++;
            }
            Constants.sections.put(name.toString(), sectionHeader);
        }

        // Parsing symtab
        SectionHeader symtab = Constants.sections.get(".symtab");
        SectionHeader strtab = Constants.sections.get(".strtab");

        for (long i = 0; i < symtab.sh_size / 16; i++) {
            Symbol symbol = new Symbol(symtab.sh_offset + i * 16, strtab.sh_offset);
            Constants.symbols.add(symbol);
            Constants.tags.put(symbol.st_value, "<" + symbol.name + ">");
        }
        // Parsing text
        SectionHeader text = Constants.sections.get(".text");
        List<Pair<Long, Instruction>> instructions = new LinkedList<>();
        long addr = header.e_entry;
        for(long off = text.sh_offset; off < text.sh_offset + text.sh_size; off += 4){
            Instruction instruction = new Instruction(off, addr);
            instructions.add(new Pair<>(addr, instruction));
            addr += 4;
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(output_file), StandardCharsets.UTF_8))) {
            writer.write(".text\n");
            for(Pair<Long, Instruction> a : instructions) {
                if(Constants.tags.containsKey(a.getFirst())) {
                    writer.write(String.format("%08x   %s:\n", a.getFirst(), Constants.tags.get(a.getFirst())));
                }
                Instruction inst = a.getSecond();
                switch (inst.type) {
                    case I -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, %s %d\n",
                            a.getFirst(), inst.bin, inst.name, inst.rd, inst.rs1, inst.imm));
                    case IJalr, ILoad -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, %d(%s)\n",
                            a.getFirst(), inst.bin, inst.name, inst.rd, inst.imm, inst.rs1));
                    case IEnv -> writer.write(String.format("   %05x:\t%08x\t%7s\n", a.getFirst(), inst.bin, inst.name));
                    case RM -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, %s, %s\n",
                            a.getFirst(), inst.bin, inst.name, inst.rd, inst.rs1, inst.rs2));
                    case S -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, %d(%s)\n",
                            a.getFirst(), inst.bin, inst.name, inst.rs2, inst.imm, inst.rs1));
                    case J -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, 0x%x %s\n",
                            a.getFirst(), inst.bin, inst.name, inst.rd, a.getFirst() + inst.imm,
                            Constants.tags.get(a.getFirst() + inst.imm)));
                    case UAui, ULui -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, %d\n",
                            a.getFirst(), inst.bin, inst.name, inst.rd, inst.imm));
                    case B -> writer.write(String.format("   %05x:\t%08x\t%7s\t%s, %s 0x%x %s\n",
                            a.getFirst(), inst.bin, inst.name, inst.rs1, inst.rs2,
                            a.getFirst() + inst.imm, Constants.tags.get(a.getFirst() + inst.imm)));
                    case unknown_instruction -> writer.write(String.format("   %05x:\t%08x\t\t%7s\n", a.getFirst(), inst.bin, inst.type));
                    default -> throw new Exception("Unknown instruction encountered while printing");
                }
            }

            writer.write("\n.symtab\n");
            writer.write("Symbol Value          	Size Type 	Bind 	Vis   	Index Name\n");
            int symInd = 0;
            for(Symbol symbol : Constants.symbols) {
                String type = switch((int) (symbol.st_info & 15)){
                    case 0  -> "NOTYPE";
                    case 1  -> "OBJECT";
                    case 2  -> "FUNC";
                    case 3  -> "SECTION";
                    case 4  -> "FILE";
                    case 5  -> "COMMON";
                    case 6  -> "TLS";
                    case 10 -> "LOOS";
                    case 12 -> "HIOS";
                    case 13 -> "LOPROC";
                    case 15 -> "HIPROC";
                    default -> throw new Exception("Unknown symbol type");
                };
                String bind = switch((int) (symbol.st_info >> 4)){
                    case 0  -> "LOCAL";
                    case 1  -> "GLOBAL";
                    case 2  -> "WEAK";
                    case 10 -> "LOOS";
                    case 12 -> "HIOS";
                    case 13 -> "LOPROC";
                    case 15 -> "HIPROC";
                    default -> throw new Exception("Unknown symbol binding");
                };
                String vis = switch((int) symbol.st_other){
                    case 0 -> "DEFAULT";
                    case 1 -> "INTERNAL";
                    case 2 -> "HIDDEN";
                    case 3 -> "PROTECTED";
                    default -> throw new Exception("Unknown symbol visibility");
                };
                String index = switch((int) symbol.st_shndx){
                    case 0xfff1 -> "ABS";
                    case 0 -> "UNDEF";
                    case 0xffff -> "XINDEX";
                    default -> Integer.toString((int) symbol.st_shndx);
                };
                writer.write(String.format("[%4d] 0x%-15X %5d %-8s %-8s %-8s %6s %s\n", symInd, symbol.st_value, symbol.st_size, type, bind, vis, index, symbol.name));
                symInd++;
            }
        } catch(Exception ex) {
            throw new Exception("Error writing to file: " + ex.getMessage());
        }
    }
}