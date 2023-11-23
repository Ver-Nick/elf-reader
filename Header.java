import java.util.Objects;

public class Header{
    public long e_type;
    public long e_machine;
    public long e_version;
    public long e_entry;
    public long e_phoff;
    public long e_shoff;
    public long e_flags;
    public long e_ehsize;
    public long e_phentsize;
    public long e_phnum;
    public long e_shentsize;
    public long e_shnum;
    public long e_shstrndx;
    public Header() throws Exception {
        e_type = Constants.extractBytes(16, 2);
        e_machine = Constants.extractBytes(18, 2);
        e_version = Constants.extractBytes(20, 4);
        e_entry = Constants.extractBytes(24, 4);
        e_phoff = Constants.extractBytes(28, 4);
        e_shoff = Constants.extractBytes(32, 4);
        e_flags = Constants.extractBytes(36, 4);
        e_ehsize = Constants.extractBytes(40, 2);
        e_phentsize = Constants.extractBytes(42, 2);
        e_phnum = Constants.extractBytes(44, 2);
        e_shentsize = Constants.extractBytes(46, 2);
        e_shnum = Constants.extractBytes(48, 2);
        e_shstrndx = Constants.extractBytes(50, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Header header = (Header) o;
        return e_type == header.e_type && e_machine == header.e_machine && e_version == header.e_version && e_entry == header.e_entry && e_phoff == header.e_phoff && e_shoff == header.e_shoff && e_flags == header.e_flags && e_ehsize == header.e_ehsize && e_phentsize == header.e_phentsize && e_phnum == header.e_phnum && e_shentsize == header.e_shentsize && e_shnum == header.e_shnum && e_shstrndx == header.e_shstrndx;
    }

    @Override
    public int hashCode() {
        return Objects.hash(e_type, e_machine, e_version, e_entry, e_phoff, e_shoff, e_flags, e_ehsize, e_phentsize, e_phnum, e_shentsize, e_shnum, e_shstrndx);
    }
};