import java.util.Objects;

public class Symbol{
    public long st_name;
    public long st_value;
    public long st_size;
    public long st_info;
    public long st_other;
    public long st_shndx;
    public String name;
    public Symbol(long offset, long strtabOffset) throws Exception {
        st_name = Constants.extractBytes(offset + 0, 4);
        st_value = Constants.extractBytes(offset + 4, 4);
        st_size = Constants.extractBytes(offset + 8, 4);
        st_info = Constants.extractBytes(offset + 12, 1);
        st_other = Constants.extractBytes(offset + 13, 1);
        st_shndx = Constants.extractBytes(offset + 14, 2);
        name = "";
        int k = (int) (strtabOffset + st_name);
        while(Constants.bytes[k] != 0){
            name += (char) Constants.bytes[k];
            k++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return st_name == symbol.st_name && st_value == symbol.st_value && st_size == symbol.st_size && st_info == symbol.st_info && st_other == symbol.st_other && st_shndx == symbol.st_shndx && Objects.equals(name, symbol.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(st_name, st_value, st_size, st_info, st_other, st_shndx, name);
    }
}