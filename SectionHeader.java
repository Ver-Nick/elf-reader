import java.util.Objects;

public class SectionHeader{
    public long sh_name;
    public long sh_type;
    public long sh_flags;
    public long sh_addr;
    public long sh_offset;
    public long sh_size;
    public long sh_link;
    public long sh_info;
    public long sh_addralign;
    public long sh_entsize;

    public SectionHeader(long e_shoff, long index, long e_shentsize) throws Exception {
        sh_name = Constants.extractBytes(e_shoff + index * e_shentsize + 0, 4);
        sh_type = Constants.extractBytes(e_shoff + index * e_shentsize + 4, 4);
        sh_flags = Constants.extractBytes(e_shoff + index * e_shentsize + 8, 4);
        sh_addr = Constants.extractBytes(e_shoff + index * e_shentsize + 12, 4);
        sh_offset = Constants.extractBytes(e_shoff + index * e_shentsize + 16, 4);
        sh_size = Constants.extractBytes(e_shoff + index * e_shentsize + 20, 4);
        sh_link = Constants.extractBytes(e_shoff + index * e_shentsize + 24, 4);
        sh_info = Constants.extractBytes(e_shoff + index * e_shentsize + 28, 4);
        sh_addralign = Constants.extractBytes(e_shoff + index * e_shentsize + 32, 4);
        sh_entsize = Constants.extractBytes(e_shoff + index * e_shentsize + 36, 4);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionHeader that = (SectionHeader) o;
        return sh_name == that.sh_name && sh_type == that.sh_type && sh_flags == that.sh_flags && sh_addr == that.sh_addr && sh_offset == that.sh_offset && sh_size == that.sh_size && sh_link == that.sh_link && sh_info == that.sh_info && sh_addralign == that.sh_addralign && sh_entsize == that.sh_entsize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sh_name, sh_type, sh_flags, sh_addr, sh_offset, sh_size, sh_link, sh_info, sh_addralign, sh_entsize);
    }
}