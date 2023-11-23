<a name="br1"></a> 

**Описание системы кодирования команд RISC-V**

**ISA** - архитектура набора команд, определяющая системную реализацию

работы процессора.

**RISC-V** — реализация **ISA**, расширяемая открытая и свободная система

команд и процессорная архитектура на основе концепции **RISC** для

микропроцессоров и микроконтроллеров. В архитектуре **RISC-V** имеется

обязательное для реализации небольшое подмножество команд и несколько

стандартных опциональных расширений.

**Базовые наборы**

• RV32I — Базовый набор с целочисленными операциями, 32-битный

• RV64I — Базовый набор с целочисленными операциями, 64-битный

• RV32E — Базовый набор с целочисленными операциями для

встраиваемых систем, 32-битный

• RV128I — Базовый набор с целочисленными операциями, 128-

битный

**Некоторые дополнительные расширения наборов**

• ~M — Целочисленное умножение и деление

• ~A — Атомарные операции (то есть операции, которые не могут быть

выполнены частично; они либо выполняются, либо нет)

• ~F — Арифметические операции с плавающей запятой над числами

одинарной точности

• ~D — Арифметические операции с плавающей запятой над числами

двойной точности

• ~Q — Арифметические операции с плавающей запятой над числами

четверной точности

• ~L — Арифметические операции с плавающей запятой над

десятичными числами

• ~C — Набор с сокращенными названиями команд

• ~B — Битовые операции

• ~J — Набор с эмуляцией набора команд с поддержкой динамической

компиляцией во время запуска

• ~P — SIMD-операции

• ~V — Векторная обработка данных (параллельно для скорости)



<a name="br2"></a> 

**Наборы RV32I и RV32M**

RV32I — базовый набор для работы с 32-битными числами, включает 39

целочисленных инструкций. Эти инструкции делят на группы: *R*, *I*, *S*, *B*, *J*,

*U*. У каждой группы свой отдельный идентификатор, записанный в виде

нескольких бит в конце каждой команды, а также отдельная структура

описания инструкции.

Выполняемая программа имеет доступ к 32 регистрам с разными именами,

с которыми может выполнять необходимые задачи.

Инструкции могут (Рис. 1):

• Совершить операцию с двумя регистрами, записать результат в

третий;

• Совершить операцию с регистром и константой, записать результат в

регистр;

• Загрузить часть регистра из памяти;

• Записать часть регистра в память;

• Перейти к заданной инструкции, если заданное условие выполняется;

• Перейти к заданной инструкции;

• Приостановить выполнение и передать контроль операционной

системе или отладчику.



<a name="br3"></a> 

Рис.1: Все инструкции базового набора RV32I.

RV32M — стандартное расширение к базовому набору RV32I,

добавляет функционал работы с умножением и делением. Даёт операции

умножения, остатка, деления нацело (Рис. 2).

Рис.2: Инструкции расширения RV32M.



<a name="br4"></a> 

**Представление инструкций**

В памяти все инструкции RV32I и RV32M представляются в виде 32 бит.

То, как именно они представляются, зависит от их типа **FMT** и заданной

этому типу структуре (см. Рис. 3)

Рис.3: Структура инструкций

В целом, почти все инструкции в **RISC-V** задаются в виде 32 бит,

кроме расширения RV32C, который сокращает инструкции до 16 бит.

Сама инструкция может быть определена по идентификатору группы

**opcode** и значению **funct3** (в случае R-типа — дополнительно по значению

**funct7**, так как количество инструкций в этой группе превышает лимит

**funct3**). Всё задается в формате *little-endian*, то есть чтобы можно было

прочитать инструкцию в формате слева направо как на табличке, четыре

байта нужно повернуть в обратном порядке (**0X**232EF4FC -> **0X**FCF42E23).

Поля **rs1** и **rs2** представляют собой регистры (\*адреса регистров) над

которыми производятся операции, **rd —** регистр для записи результата

работы операции над **rs1** и **rs2**. Поле **imm** в зависимости от типа может

представлять собой как константу для совершения с ней операции над

регистром, так и значение, на которое должен быть совершен переход,

например в операциях типа *B* и *J.*

Регистры, над которыми совершают операции имеют свои имена и

предназначение (см. Рис. 4). Первые 16 идут в использование набору RV32I,

остальные 16 предназначены для работы с нецелыми числами, например

для расширений RV32F или RV32F, поэтому здесь рассматриваться не

будут.

Поле **imm** имеет такую странную структуру с разными частями в

совершенно разных позициях инструкции по той причине, что на уровне

аппаратной реализации эти значения извлекает мультиплексор; данная

реализация помогает уменьшить количество раз, которое мультиплексор

затрачивает на получение значений, уменьшая коллизии битов;



<a name="br5"></a> 

соответственно в некоторых случаях можно однозначно определить

некоторые биты, не прочитав их, по другим уже прочитанным битам. Это в

итоге повышает общую производительность.

Рис.4: имена регистров, их идентификаторы в полях **rs1**, **rs2** и **rd**



<a name="br6"></a> 

**ELF файлы**

*Эльфы, Старшие Дети этого мира, были племенем прекрасным и*

*благородным; владыками их были Эльдар, ныне покинувшие эту землю, – Народ*

*Великого Перехода, Народ Звёзд. (c) Властелин Колец*

**ELF —** формат исполняемых двоичных файлов, используемый во многих современных

UNIX-подобных операционных системах, таких как FreeBSD, Linux и Solaris.

Обычно ELF делится на два типа — один для 32-разрядной архитектуры и

второй, позже появившийся, для 64-битной. Здесь мы будем рассматривать

32-разрядную архитектуру.

**Заголовок файла**

В 32-битной реализации состоит из 52 байт, описывает самые важные

поля, такие как начало выполняемого кода, начало полей с информацией и

количество этой информации:

• *e\_ident* — содержит некоторую информацию по файлу и

архитектуре. Первые четыре байта обязаны быть 0x7f, 0x45,

0x4c и 0x46, иначе нас обманули и это не ELF файл;

• *e\_type* — тип исполняемого файла;

• *e\_machine* — тип архитектуры, для реализации RISC-V будет

0xF3;

• *e\_version* — версия, на данный момент всегда 1;

• *e\_entry —* адрес, откуда начинается выполнение файла;

• *e\_phoff —* позиция таблицы заголовков программы (в данной

работе эта таблица не нужна, но необходима операционной

системе для подготовки к запуску);

• *e\_shoff —* позиция таблицы заголовков секций;

• *e\_flags —* связанные с файлом флаги для использования

процессором;

• *e\_ehsize —* размер заголовка файла, для 32-битной реализации

равен 52;

• *e\_phentsize —* размер одного заголовка программы;

• *e\_phnum —* число заголовков программы;

• *e\_shentsize —* размер одного заголовка секции;

• *e\_shnum —* число заголовков секции;

• *e\_shstrndx —* индекс записи в таблице заголовков секций,

описывающей таблицу названий секций.



<a name="br7"></a> 

**Таблица заголовков секций**

Таблица заголовков секций содержит атрибуты секций файла. Секции

передают информацию, либо для исполнения, либо для использования

другими секциями. Каждый заголовок секции описывает отдельную

секцию:

• *sh\_name* — указатель на имя секции в таблице названий секций,

о ней будет сказано позже;

• *sh\_type* — тип секции;

• *sh\_flags* — атрибуты секции;

• *sh\_addr* — при необходимости предварительной загрузки

секции указывает адрес, куда её можно загрузить;

• *sh\_offset* — расположение секции относительно начала файла;

• *sh\_size* — размер секции в байтах;

• *sh\_link* — индекс ассоциированной секции;

• *sh\_info* — другая дополнительная информация;

• *sh\_addralign* — выравнивание секции;

• *sh\_entsize —* размер в байтах каждого элемента в секции.

**Секции**

Секции описывают различные части файла, в итоге формируя исполняемый

файл. Сфокусируемся на *.strtab*, *.symtab* и *.text*:

• *.strtab* — содержит список строк, разделенных нулями. Эти строки

являются именами секций и именами символов из секции *.symtab*,

представленных в заданном ELF файле. Именно в этой секции другие

секции указывают на своё имя полем *sh\_name.*

• *.symtab* — в этой секции содержатся все символы, которые

компоновщик использует как во время компиляции, так и во время

выполнения приложения. В реализации 32-битного ELF файла

содержит 16-байтные структуры, называемые символами и имеющие

имя, значение, размер и другую информацию. Инструкции RISC-V в

итоге используют эти символы для описания блока функции.

• *.text* — сама исполняемая программа; содержит 4-байтные блоки

инструкций, в данном случае инструкции заданы в системе

кодирования **RISC-V**.



<a name="br8"></a> 

**Описание работы написанного кода**

**Общее описание**

Код был написан на Java с использованием JDK 19.

Все важные части: инструкции, секции, символы, заголовки описаны

своими классами и при создании достают по заданному адресу

информацию о себе и записывают для дальнейшего использования. Также

описан общий класс Pair для удобства, так как, к сожалению, язык не

поддерживает данную структуру в отличие от C++.

Все описываемые файлы находятся на одном уровне с запускаемым

файлом Main.java.

Существует ещё один файл, Constants.java, содержащий необходимые

переменные, константы, словари, к которым исполняемый код обращается

по мере надобности. Тут же и описана функция extractBytes, которая читает

информацию из файла в формате little-endian:

public static long extractBytes(long offset, long byteNum) throws Exception {

if (byteNum <= 0 || byteNum > 4) {

throw new Exception("Wrong number of bytes to read in extractBytes()");

}

long result = 0;

for(long i = 0; i < byteNum; i++){

if (offset + byteNum - i - 1 < 0 || offset + byteNum - i - 1 >= *TOTAL*) {

throw new Exception("Index out of boundaries when trying to read in

extractBytes()");

}

result = result \* 256 + (*bytes*[(int) (offset + byteNum - i - 1)]);

}

return result;

}

Эта функция используется, например в классе Header.java для

читания ELF файла:

public Header() throws Exception {

e\_type = Constants.*extractBytes*(16, 2);

e\_machine = Constants.*extractBytes*(18, 2);

e\_version = Constants.*extractBytes*(20, 4);

e\_entry = Constants.*extractBytes*(24, 4);

e\_phoff = Constants.*extractBytes*(28, 4);

e\_shoff = Constants.*extractBytes*(32, 4);

e\_flags = Constants.*extractBytes*(36, 4);

e\_ehsize = Constants.*extractBytes*(40, 2);

e\_phentsize = Constants.*extractBytes*(42, 2);

e\_phnum = Constants.*extractBytes*(44, 2);

e\_shentsize = Constants.*extractBytes*(46, 2);

e\_shnum = Constants.*extractBytes*(48, 2);

e\_shstrndx = Constants.*extractBytes*(50, 2);

}



<a name="br9"></a> 

Ещё есть функция для преобразования беззнакового типа в знаковый,

используется для преобразования поля **imm:**

public static long immToSigned(long n, long where){

if ((n & (1L << (where - 1))) != 0) {

return (n | -(1L << where));

}

else {

return n;

}

}

И функция которая помогает выделить необходимые биты из данных:

public static long cutInstruction(long bin, long from, long to) {

bin = bin & ((1L << (from + 1)) - 1);

bin = bin & (-(1L << to));

return (bin >> to);

}

Также в Constants.java описаны словари для каждого **FMT** типа,

которые по полям **funct3** и **funct7** инструкции RISC-V однозначно

определяют операцию. Например, для типа *S* словарь имеет следующий

вид:

public static final Map<Pair<Integer, Integer>, String> *FmtS* = Map.*ofEntries*(

*entry*(new Pair<>(0, 0), "sb"),

*entry*(new Pair<>(0x1, 0), "sh"),

*entry*(new Pair<>(0x2, 0), "sw")

);

**Парсинг**

Берём аргументы командной строки, если мало — ошибка. Проверяем

наличие файлов читки и записи, если кого-то не существует — ошибка.

Далее читаем поток байтов в массив Constants.bytes, преобразуем из signed

byte в положительный long. Даём классу Header прочитать свои 52 байта и

выделить поля, проверяем некоторые из них, такие как первые четыре

**магических** бита, тип архитектуры, размер заголовка, если что-то не

совпадает — ошибка.

Далее, читаем все заголовки секций классами SectionHeader, ищем их

настоящие имена в *.strtab*, который лежит по позиции найденного ранее

поля e\_shstrndx из заголовка файла. Для удобства ложим каждую секцию в

словарь по ключу имени из *.strtab* и значению самого класса секции.

Далее, достанем из этого словаря секции *.strtab* и *.symtab*. Теперь

можно достать все символы классом Symbol из *.symtab* и соответствующие



<a name="br10"></a> 

им имена из *.strtab*. Также добавим символы в словарь для дальнейшего

использования инструкиями.

Теперь можно достать секцию *.text*, где собственно и будет

выполняться само дизассемблирование. Выделяя по четыре байта, передаем

парсинг классу Instruction, который, используя словари **FMT** типов,

определяет операции и читает их поля соответствующе своему типу.

Определив тип, он находит имена используемых регистров. Если вдруг это

инструкция, подразумевающая переход (типа *B* или *J*), то необходимо ещё

проверить наличие метки (символа из *.symtab*) в словаре (про который было

сказано выше) по адресу с переходом. Если её нет, то добавим её в словарь

по заданному правилу, после чего увеличим счётчик этих локальных меток.

Добавим инструкцию в список чтобы в конце вывести их все.

Для вывода сначала проходим по списку символов (не словарю с

добавленными дополнительно локальными метками,

а

именно

изначальному списку из *.symtab*), выводим каждый по заданному шаблону.

Далее берём список инструкций. Если вдруг адрес инструкции указывает на

адрес метки в словаре, то выводим её. Независимо от наличия метки

выводим саму инструкцию согласно её типу и заданному формату.

В процессе всех действий в случае любой непонятной ситуации резко

бросаем ошибку.

**Компиляция и запуск**

Компиляцию можно произвести командой ***javac Main.java***

Запуск командой ***java Main rv3 input\_file output\_file***



<a name="br11"></a> 

**Результат работы на заданном файле**

.text

00010074 <main>:

ff010113 addi sp, sp -16

00112623 sw ra, 12(sp)

030000ef jal ra, 0x100ac <mmul>

00c12083 lw ra, 12(sp)

10074:

10078:

1007c:

10080:

10084:

10088:

1008c:

10090:

10094:

10098:

1009c:

100a0:

100a4:

100a8:

00000513 addi a0, zero 0

01010113 addi sp, sp 16

00008067 jalr zero, 0(ra)

00000013 addi zero, zero 0

00100137 lui sp, 256

fddff0ef jal ra, 0x10074 <main>

00050593 addi a1, a0 0

00a00893 addi a7, zero 10

0ff0000f

unknown\_instruction

00000073 ecall

000100ac <mmul>:

100ac:

100b0:

00011f37 lui t5, 17

124f0513 addi a0, t5 292

100b4:

65450513 addi a0, a0 1620

124f0f13 addi t5, t5 292

e4018293 addi t0, gp -448

fd018f93 addi t6, gp -48

02800e93 addi t4, zero 40

100b8:

100bc:

100c0:

100c4:

000100c8 <L2>:

100c8:

fec50e13 addi t3, a0 -20

000f0313 addi t1, t5 0

000f8893 addi a7, t6 0

00000813 addi a6, zero 0

100cc:

100d0:

100d4:

000100d8 <L1>:

100d8:

00088693 addi a3, a7 0

000e0793 addi a5, t3 0

100dc:



<a name="br12"></a> 

100e0:

000100e4 <L0>:

100e4:

00000613 addi a2, zero 0

00078703

00069583

lb a4, 0(a5)

lh a1, 0(a3)

100e8:

100ec:

00178793 addi a5, a5 1

100f0:

02868693 addi a3, a3 40

02b70733 mul a4, a4, a1

00e60633 add a2, a2, a4

fea794e3 bne a5, a0 0x100e4 <L0>

100f4:

100f8:

100fc:

10100:

00c32023

sw a2, 0(t1)

10104:

00280813 addi a6, a6 2

10108:

00430313 addi t1, t1 4

1010c:

00288893 addi a7, a7 2

10110:

fdd814e3 bne a6, t4 0x100d8 <L1>

050f0f13 addi t5, t5 80

01478513 addi a0, a5 20

fa5f16e3 bne t5, t0 0x100c8 <L2>

00008067 jalr zero, 0(ra)

10114:

10118:

1011c:

10120:

Symbol Value

[ 0] 0x0

[ 1] 0x10074

[ 2] 0x11124

[ 3] 0x0

[ 4] 0x0

[ 5] 0x0

[ 6] 0x11924

[ 7] 0x118F4

[ 8] 0x11124

[ 9] 0x100AC

[ 10] 0x0

[ 11] 0x11124

[ 12] 0x11C14

[ 13] 0x11124

[ 14] 0x10074

Size Type

Bind Vis

Index Name

0 NOTYPE LOCAL DEFAULT UNDEF

0 SECTION LOCAL DEFAULT

0 SECTION LOCAL DEFAULT

0 SECTION LOCAL DEFAULT

0 SECTION LOCAL DEFAULT

1

2

3

4

0 FILE

LOCAL DEFAULT

ABS test.c

0 NOTYPE GLOBAL DEFAULT

800 OBJECT GLOBAL DEFAULT

0 NOTYPE GLOBAL DEFAULT

ABS \_\_global\_pointer$

2 b

1 \_\_SDATA\_BEGIN\_\_

1 mmul

120 FUNC

GLOBAL DEFAULT

0 NOTYPE GLOBAL DEFAULT UNDEF \_start

1600 OBJECT GLOBAL DEFAULT

0 NOTYPE GLOBAL DEFAULT

0 NOTYPE GLOBAL DEFAULT

2 c

2 \_\_BSS\_END\_\_

2 \_\_bss\_start

1 main

28 FUNC

GLOBAL DEFAULT



<a name="br13"></a> 

[ 15] 0x11124

[ 16] 0x11124

[ 17] 0x11C14

[ 18] 0x11764

0 NOTYPE GLOBAL DEFAULT

0 NOTYPE GLOBAL DEFAULT

0 NOTYPE GLOBAL DEFAULT

400 OBJECT GLOBAL DEFAULT

1 \_\_DATA\_BEGIN\_\_

1 \_edata

2 \_end

2 a



<a name="br14"></a> 

**Список источников**

<https://en.wikipedia.org/wiki/RISC-V>

[https://blog.k3170makan.com/2018/10/introduction-to-elf-format-part-](https://blog.k3170makan.com/2018/10/introduction-to-elf-format-part-vi.html)

[vi.html](https://blog.k3170makan.com/2018/10/introduction-to-elf-format-part-vi.html)

<https://github.com/jameslzhu/riscv-card/blob/master/riscv-card.pdf>

<https://refspecs.linuxbase.org/elf/gabi4+/ch4.symtab.html>

<https://en.wikipedia.org/wiki/Instruction_set_architecture>

<https://riscv.org/technical/specifications/>



<a name="br15"></a> 

**Листинг кода**

**Main.java**

import java.io.\*;

import java.nio.charset.StandardCharsets;

import java.util.LinkedList;

import java.util.List;

public class Main {

public static void main(String[] args) throws Exception {

if(args.length < 3){

throw new Exception("Not enough arguments");

}

String input\_file = args[1];

String output\_file = args[2];

File f1 = new File(input\_file);

if(!f1.exists() || f1.isDirectory()) {

System.*out*.println("Input file not found");

return;

}

File f2 = new File(output\_file);

if(!f2.exists() || f2.isDirectory()) {

System.*out*.println("Output file not found");

return;

}

byte[] bytes\_tmp;

try (DataInputStream reader = new DataInputStream(new

FileInputStream(input\_file))) {

bytes\_tmp = reader.readAllBytes();

} catch(Exception ex) {

throw new Exception("Error reading file: " + ex.getMessage());

}

Constants.*TOTAL* = bytes\_tmp.length;

Constants.*bytes* = new long[(int) Constants.*TOTAL*];

for(long i = 0; i < Constants.*TOTAL*; i++) {

long c = ((bytes\_tmp[(int) i] < 0) ? 256 : 0) + bytes\_tmp[(int) i];

Constants.*bytes*[(int) i] = c;

}

long magic = Constants.*extractBytes*(0, 4);

if(magic != Constants.*ELF\_MAGIC*) {

throw new Exception("Not an ELF file");

}

*// Extract elf header fields*

Header header = new Header();

if(header.e\_machine != 0xF3) {

throw new Exception("File is not for RISC-V architecture");

}

if(header.e\_version != 1) {

throw new Exception("Wrong file version");

}



<a name="br16"></a> 

if(header.e\_ehsize != 52) {

throw new Exception("Wrong header size");

}

*// Parse section headers and their names*

SectionHeader sectionTable = new SectionHeader(header.e\_shoff,

header.e\_shstrndx, header.e\_shentsize);

for(long i = 0; i < header.e\_shnum; i++){

SectionHeader sectionHeader = new SectionHeader(header.e\_shoff, i,

header.e\_shentsize);

long pos = sectionTable.sh\_offset + sectionHeader.sh\_name;

StringBuilder name = new StringBuilder();

while(Constants.*bytes*[(int) pos] != 0){

name.append((char) Constants.*bytes*[(int) pos]);

pos++;

}

Constants.*sections*.put(name.toString(), sectionHeader);

}

*// Parsing symtab*

SectionHeader symtab = Constants.*sections*.get(".symtab");

SectionHeader strtab = Constants.*sections*.get(".strtab");

for (long i = 0; i < symtab.sh\_size / 16; i++) {

Symbol symbol = new Symbol(symtab.sh\_offset + i \* 16,

strtab.sh\_offset);

Constants.*symbols*.add(symbol);

Constants.*tags*.put(symbol.st\_value, "<" + symbol.name + ">");

}

*// Parsing text*

SectionHeader text = Constants.*sections*.get(".text");

List<Pair<Long, Instruction>> instructions = new LinkedList<>();

long addr = header.e\_entry;

for(long off = text.sh\_offset; off < text.sh\_offset + text.sh\_size; off +=

4){

Instruction instruction = new Instruction(off, addr);

instructions.add(new Pair<>(addr, instruction));

addr += 4;

}

try (Writer writer = new BufferedWriter(new OutputStreamWriter(

new FileOutputStream(output\_file), StandardCharsets.*UTF\_8*))) {

writer.write(".text\n");

for(Pair<Long, Instruction> a : instructions) {

if(Constants.*tags*.containsKey(a.getFirst())) {

writer.write(String.*format*("%08x %s:\n", a.getFirst(),

Constants.*tags*.get(a.getFirst())));

}

Instruction inst = a.getSecond();

switch (inst.type) {

case *I* -> writer.write(String.*format*(" %05x:\t%08x\t%7s\t%s,

%s %d\n",

a.getFirst(), inst.bin, inst.name, inst.rd, inst.rs1,

inst.imm));

case *IJalr*, *ILoad* -> writer.write(String.*format*("

%05x:\t%08x\t%7s\t%s, %d(%s)\n",



<a name="br17"></a> 

a.getFirst(), inst.bin, inst.name, inst.rd, inst.imm,

inst.rs1));

case *IEnv* -> writer.write(String.*format*("

%05x:\t%08x\t%7s\n", a.getFirst(), inst.bin, inst.name));

case *RM* -> writer.write(String.*format*("

%05x:\t%08x\t%7s\t%s, %s, %s\n",

a.getFirst(), inst.bin, inst.name, inst.rd, inst.rs1,

inst.rs2));

%d(%s)\n",

inst.rs1));

0x%x %s\n",

case *S* -> writer.write(String.*format*(" %05x:\t%08x\t%7s\t%s,

a.getFirst(), inst.bin, inst.name, inst.rs2, inst.imm,

case *J* -> writer.write(String.*format*(" %05x:\t%08x\t%7s\t%s,

a.getFirst(), inst.bin, inst.name, inst.rd,

a.getFirst() + inst.imm,

Constants.*tags*.get(a.getFirst() + inst.imm)));

case *UAui*, *ULui* -> writer.write(String.*format*("

%05x:\t%08x\t%7s\t%s, %d\n",

a.getFirst(), inst.bin, inst.name, inst.rd,

inst.imm));

case *B* -> writer.write(String.*format*(" %05x:\t%08x\t%7s\t%s,

%s 0x%x %s\n",

a.getFirst(), inst.bin, inst.name, inst.rs1, inst.rs2,

a.getFirst() + inst.imm,

Constants.*tags*.get(a.getFirst() + inst.imm)));

case *unknown\_instruction* -> writer.write(String.*format*("

%05x:\t%08x\t\t%7s\n", a.getFirst(), inst.bin, inst.type));

default -> throw new Exception("Unknown instruction

encountered while printing");

}

}

writer.write("Symbol Value

Size Type Bind Vis

Index

Name\n");

int symInd = 0;

for(Symbol symbol : Constants.*symbols*) {

String type = switch((int) (symbol.st\_info & 15)){

case 0 -> "NOTYPE";

case 1 -> "OBJECT";

case 2 -> "FUNC";

case 3 -> "SECTION";

case 4 -> "FILE";

case 5 -> "COMMON";

case 6 -> "TLS";

case 10 -> "LOOS";

case 12 -> "HIOS";

case 13 -> "LOPROC";

case 15 -> "HIPROC";

default -> throw new Exception("Unknown symbol type");

};

String bind = switch((int) (symbol.st\_info >> 4)){

case 0 -> "LOCAL";

case 1 -> "GLOBAL";

case 2 -> "WEAK";

case 10 -> "LOOS";

case 12 -> "HIOS";

case 13 -> "LOPROC";

case 15 -> "HIPROC";

default -> throw new Exception("Unknown symbol binding");



<a name="br18"></a> 

};

String vis = switch((int) symbol.st\_other){

case 0 -> "DEFAULT";

case 1 -> "INTERNAL";

case 2 -> "HIDDEN";

case 3 -> "PROTECTED";

default -> throw new Exception("Unknown symbol visibility");

};

String index = switch((int) symbol.st\_shndx){

case 0xfff1 -> "ABS";

case 0 -> "UNDEF";

case 0xffff -> "XINDEX";

default -> Integer.*toString*((int) symbol.st\_shndx);

};

writer.write(String.*format*("[%4d] 0x%-15X %5d %-8s %-8s %-8s %6s

%s\n", symInd, symbol.st\_value, symbol.st\_size, type, bind, vis, index,

symbol.name));

symInd++;

}

} catch(Exception ex) {

throw new Exception("Error writing to file: " + ex.getMessage());

}

}

}

**Instruction.java**

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

bin = Constants.*extractBytes*(offset, 4);

long opcode = Constants.*cutInstruction*(bin, 6, 0);

name = "unknown instruction";

type = Constants.FMT.*unknown\_instruction*;

if(opcode == 0b0110011){

type = Constants.FMT.*RM*;

long rdId = Constants.*cutInstruction*(bin, 11, 7);

long funct3 = Constants.*cutInstruction*(bin, 14, 12);

long funct7 = Constants.*cutInstruction*(bin, 31, 25);

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

long rs2Id = Constants.*cutInstruction*(bin, 24, 20);

name = Constants.*FmtRM*.get(new Pair<>((int)funct3, (int)funct7));

imm = 0;

rd = Constants.*registerNames*.get((int)rdId);

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = Constants.*registerNames*.get((int)rs2Id);

}

if(opcode == 0b0010011) {

type = Constants.FMT.*I*;



<a name="br19"></a> 

long rdId = Constants.*cutInstruction*(bin, 11, 7);

long funct3 = Constants.*cutInstruction*(bin, 14, 12);

long funct7 = 0;

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

imm = Constants.*cutInstruction*(bin, 31, 20);

imm = Constants.*immToSigned*(imm, 12);

if(funct3 == 0x5) {

funct7 = Constants.*cutInstruction*(bin, 31, 25);

}

name = Constants.*FmtI*.get(new Pair<>((int)funct3, (int)funct7));

rd = Constants.*registerNames*.get((int)rdId);

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = "";

}

if(opcode == 0b0000011) {

type = Constants.FMT.*ILoad*;

long rdId = Constants.*cutInstruction*(bin, 11, 7);

long funct3 = Constants.*cutInstruction*(bin, 14, 12);

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

imm = Constants.*immToSigned*(Constants.*cutInstruction*(bin, 31, 20),

12);

name = Constants.*FmtILoad*.get(new Pair<>((int)funct3, 0));

rd = Constants.*registerNames*.get((int)rdId);

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = "";

}

if(opcode == 0b1100111) {

type = Constants.FMT.*IJalr*;

long rdId = Constants.*cutInstruction*(bin, 11, 7);

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

imm = Constants.*immToSigned*(Constants.*cutInstruction*(bin, 31, 20),

12);

name = "jalr";

rd = Constants.*registerNames*.get((int)rdId);

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = "";

}

if(opcode == 0b1110011) {

type = Constants.FMT.*IEnv*;

long rdId = Constants.*cutInstruction*(bin, 11, 7);

long funct7 = Constants.*cutInstruction*(bin, 31, 20);

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

imm = Constants.*immToSigned*(Constants.*cutInstruction*(bin, 31, 20),

12);

name = (funct7 == 0 ? "ecall" : "ebreak");

rd = Constants.*registerNames*.get((int)rdId);

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = "";

}

if(opcode == 0b0100011) {

type = Constants.FMT.*S*;

imm = 0;

imm |= (Constants.*cutInstruction*(bin, 31, 25) >> 5);

imm |= Constants.*cutInstruction*(bin, 11, 7);



<a name="br20"></a> 

long funct3 = Constants.*cutInstruction*(bin, 14, 12);

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

long rs2Id = Constants.*cutInstruction*(bin, 24, 20);

name = Constants.*FmtS*.get(new Pair<>((int)funct3, 0));

imm = Constants.*immToSigned*(imm, 12);

rd = "";

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = Constants.*registerNames*.get((int)rs2Id);

}

if(opcode == 0b1100011) {

type = Constants.FMT.*B*;

long imm12\_105 = Constants.*cutInstruction*(bin, 31, 25);

long imm41\_11 = Constants.*cutInstruction*(bin, 11, 7);

long imm12th = Constants.*cutInstruction*(imm12\_105, 6, 6);

long imm105 = Constants.*cutInstruction*(imm12\_105, 5, 0);

long imm41 = Constants.*cutInstruction*(imm41\_11, 4, 1);

long imm11th = Constants.*cutInstruction*(imm41\_11, 0, 0);

imm = 0;

imm |= (imm12th << 12);

imm |= (imm11th << 11);

imm |= (imm105 << 5);

imm |= (imm41 << 1);

long funct3 = Constants.*cutInstruction*(bin, 14, 12);

long rs1Id = Constants.*cutInstruction*(bin, 19, 15);

long rs2Id = Constants.*cutInstruction*(bin, 24, 20);

name = Constants.*FmtB*.get(new Pair<>((int)funct3, 0));

imm = Constants.*immToSigned*(imm, 13);

if(!Constants.*tags*.containsKey(address + imm)){

String nextTag = "<L" + Constants.*TAGINDEX* + ">";

Constants.*tags*.put(address + imm, nextTag);

Constants.*TAGINDEX*++;

}

rd = "";

rs1 = Constants.*registerNames*.get((int)rs1Id);

rs2 = Constants.*registerNames*.get((int)rs2Id);

}

if(opcode == 0b1101111) {

type = Constants.FMT.*J*;

imm = 0;

long imm20th = Constants.*cutInstruction*(bin, 31, 31);

long imm101 = Constants.*cutInstruction*(bin, 30, 21);

long imm11th = Constants.*cutInstruction*(bin, 20, 20);

long imm1912 = Constants.*cutInstruction*(bin, 19, 12);

imm |= (imm20th << 20);

imm |= (imm101 << 1);

imm |= (imm11th << 11);

imm |= (imm1912 << 12);

imm = Constants.*immToSigned*(imm, 21);

long rdId = Constants.*cutInstruction*(bin, 11, 7);

name = "jal";

if(!Constants.*tags*.containsKey(address + imm)) {



<a name="br21"></a> 

String nextTag = "<L" + Constants.*TAGINDEX* + ">";

Constants.*tags*.put(address + imm, nextTag);

Constants.*TAGINDEX*++;

}

imm = Constants.*immToSigned*(imm, 21);

rd = Constants.*registerNames*.get((int)rdId);

rs1 = "";

rs2 = "";

}

if(opcode == 0b0110111) {

type = Constants.FMT.*ULui*;

name = "lui";

imm = Constants.*immToSigned*(Constants.*cutInstruction*(bin, 31, 12),

21);

long rdId = Constants.*cutInstruction*(bin, 11, 7);

rd = Constants.*registerNames*.get((int)rdId);

rs1 = "";

rs2 = "";

}

if(opcode == 0b0010111) {

type = Constants.FMT.*UAui*;

name = "auipc";

imm = Constants.*immToSigned*(Constants.*cutInstruction*(bin, 31, 12),

21);

long rdId = Constants.*cutInstruction*(bin, 11, 7);

rd = Constants.*registerNames*.get((int)rdId);

rs1 = "";

rs2 = "";

}

}

@Override

public boolean equals(Object o) {

if (this == o) return true;

if (o == null || getClass() != o.getClass()) return false;

Instruction that = (Instruction) o;

return imm == that.imm && bin == that.bin && Objects.*equals*(name,

that.name) && Objects.*equals*(rd, that.rd) && Objects.*equals*(rs1, that.rs1) &&

Objects.*equals*(rs2, that.rs2) && type == that.type;

}

@Override

public int hashCode() {

return Objects.*hash*(name, rd, rs1, rs2, imm, type, bin);

}

}

**Symbol.java**

import java.util.Objects;

public class Symbol{

public long st\_name;

public long st\_value;

public long st\_size;

public long st\_info;

public long st\_other;

public long st\_shndx;



<a name="br22"></a> 

public String name;

public Symbol(long offset, long strtabOffset) throws Exception {

st\_name = Constants.*extractBytes*(offset + 0, 4);

st\_value = Constants.*extractBytes*(offset + 4, 4);

st\_size = Constants.*extractBytes*(offset + 8, 4);

st\_info = Constants.*extractBytes*(offset + 12, 1);

st\_other = Constants.*extractBytes*(offset + 13, 1);

st\_shndx = Constants.*extractBytes*(offset + 14, 2);

name = "";

int k = (int) (strtabOffset + st\_name);

while(Constants.*bytes*[k] != 0){

name += (char) Constants.*bytes*[k];

k++;

}

}

@Override

public boolean equals(Object o) {

if (this == o) return true;

if (o == null || getClass() != o.getClass()) return false;

Symbol symbol = (Symbol) o;

return st\_name == symbol.st\_name && st\_value == symbol.st\_value && st\_size

== symbol.st\_size && st\_info == symbol.st\_info && st\_other == symbol.st\_other &&

st\_shndx == symbol.st\_shndx && Objects.*equals*(name, symbol.name);

}

@Override

public int hashCode() {

return Objects.*hash*(st\_name, st\_value, st\_size, st\_info, st\_other,

st\_shndx, name);

}

}

**Header.java**

import java.util.Objects;

public class Header{

public long e\_type;

public long e\_machine;

public long e\_version;

public long e\_entry;

public long e\_phoff;

public long e\_shoff;

public long e\_flags;

public long e\_ehsize;

public long e\_phentsize;

public long e\_phnum;

public long e\_shentsize;

public long e\_shnum;

public long e\_shstrndx;

public Header() throws Exception {

e\_type = Constants.*extractBytes*(16, 2);

e\_machine = Constants.*extractBytes*(18, 2);

e\_version = Constants.*extractBytes*(20, 4);

e\_entry = Constants.*extractBytes*(24, 4);

e\_phoff = Constants.*extractBytes*(28, 4);

e\_shoff = Constants.*extractBytes*(32, 4);

e\_flags = Constants.*extractBytes*(36, 4);



<a name="br23"></a> 

e\_ehsize = Constants.*extractBytes*(40, 2);

e\_phentsize = Constants.*extractBytes*(42, 2);

e\_phnum = Constants.*extractBytes*(44, 2);

e\_shentsize = Constants.*extractBytes*(46, 2);

e\_shnum = Constants.*extractBytes*(48, 2);

e\_shstrndx = Constants.*extractBytes*(50, 2);

}

@Override

public boolean equals(Object o) {

if (this == o) return true;

if (o == null || getClass() != o.getClass()) return false;

Header header = (Header) o;

return e\_type == header.e\_type && e\_machine == header.e\_machine &&

e\_version == header.e\_version && e\_entry == header.e\_entry && e\_phoff ==

header.e\_phoff && e\_shoff == header.e\_shoff && e\_flags == header.e\_flags &&

e\_ehsize == header.e\_ehsize && e\_phentsize == header.e\_phentsize && e\_phnum ==

header.e\_phnum && e\_shentsize == header.e\_shentsize && e\_shnum == header.e\_shnum

&& e\_shstrndx == header.e\_shstrndx;

}

@Override

public int hashCode() {

return Objects.*hash*(e\_type, e\_machine, e\_version, e\_entry, e\_phoff,

e\_shoff, e\_flags, e\_ehsize, e\_phentsize, e\_phnum, e\_shentsize, e\_shnum,

e\_shstrndx);

}

};

**SectionHeader.java**

import java.util.Objects;

public class SectionHeader{

public long sh\_name;

public long sh\_type;

public long sh\_flags;

public long sh\_addr;

public long sh\_offset;

public long sh\_size;

public long sh\_link;

public long sh\_info;

public long sh\_addralign;

public long sh\_entsize;

public SectionHeader(long e\_shoff, long index, long e\_shentsize) throws

Exception {

sh\_name = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 0, 4);

sh\_type = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 4, 4);

sh\_flags = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 8, 4);

sh\_addr = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 12, 4);

sh\_offset = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 16, 4);

sh\_size = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 20, 4);

sh\_link = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 24, 4);

sh\_info = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 28, 4);

sh\_addralign = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 32,

4);

sh\_entsize = Constants.*extractBytes*(e\_shoff + index \* e\_shentsize + 36,



<a name="br24"></a> 

4);

}

@Override

public boolean equals(Object o) {

if (this == o) return true;

if (o == null || getClass() != o.getClass()) return false;

SectionHeader that = (SectionHeader) o;

return sh\_name == that.sh\_name && sh\_type == that.sh\_type && sh\_flags ==

that.sh\_flags && sh\_addr == that.sh\_addr && sh\_offset == that.sh\_offset && sh\_size

== that.sh\_size && sh\_link == that.sh\_link && sh\_info == that.sh\_info &&

sh\_addralign == that.sh\_addralign && sh\_entsize == that.sh\_entsize;

}

@Override

public int hashCode() {

return Objects.*hash*(sh\_name, sh\_type, sh\_flags, sh\_addr, sh\_offset,

sh\_size, sh\_link, sh\_info, sh\_addralign, sh\_entsize);

}

}

**Constants.java**

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

import static java.util.Map.*entry*;

public final class Constants {

public static final long *ELF\_MAGIC* = 0x464C457F;

public static long[] *bytes*;

public static long *TAGINDEX* = 0;

public static long *TOTAL*;

public static Map<String, SectionHeader> *sections* = new HashMap<>();

public static Map<Long, String> *tags* = new HashMap<Long, String>();

public static List<Symbol> *symbols* = new ArrayList<>();

public static final Map<Pair<Integer, Integer>, String> *FmtRM* = Map.*ofEntries*(

*entry*(new Pair<>(0, 0), "add"),

*entry*(new Pair<>(0,0x20), "sub"),

*entry*(new Pair<>(0x4,0), "xor"),

*entry*(new Pair<>(0x6,0), "or"),

*entry*(new Pair<>(0x7,0), "and"),

*entry*(new Pair<>(0x1,0), "sli"),

*entry*(new Pair<>(0x5,0), "srl"),

*entry*(new Pair<>(0x5,0x20), "sra"),

*entry*(new Pair<>(0x2,0), "slt"),

*entry*(new Pair<>(0x3,0), "sltu"),

*entry*(new Pair<>(0x0,0x1), "mul"),

*entry*(new Pair<>(0x1,0x1), "mulh"),

*entry*(new Pair<>(0x2,0x1), "mulsu"),

*entry*(new Pair<>(0x3,0x1), "mulu"),

*entry*(new Pair<>(0x4,0x1), "div"),

*entry*(new Pair<>(0x5,0x1), "divu"),

*entry*(new Pair<>(0x6,0x1), "rem"),

*entry*(new Pair<>(0x7,0x1), "remu")



<a name="br25"></a> 

);

public static final Map<Pair<Integer, Integer>, String> *FmtI* = Map.*ofEntries*(

*entry*(new Pair<>(0, 0), "addi"),

*entry*(new Pair<>(0x4, 0), "xori"),

*entry*(new Pair<>(0x6, 0), "ori"),

*entry*(new Pair<>(0x7, 0), "0"),

*entry*(new Pair<>(0x1, 0), "slli"),

*entry*(new Pair<>(0x5, 0), "srli"),

*entry*(new Pair<>(0x5, 0x20), "srai"),

*entry*(new Pair<>(0x2, 0), "slti"),

*entry*(new Pair<>(0x3, 0), "sltiu")

);

public static final Map<Pair<Integer, Integer>, String> *FmtILoad* =

Map.*ofEntries*(

*entry*(new Pair<>(0, 0), "lb"),

*entry*(new Pair<>(0x1, 0), "lh"),

*entry*(new Pair<>(0x2, 0), "lw"),

*entry*(new Pair<>(0x4, 0), "lbu"),

*entry*(new Pair<>(0x5, 0), "lhu")

);

public static final Map<Pair<Integer, Integer>, String> *FmtS* = Map.*ofEntries*(

*entry*(new Pair<>(0, 0), "sb"),

*entry*(new Pair<>(0x1, 0), "sh"),

*entry*(new Pair<>(0x2, 0), "sw")

);

public static final Map<Pair<Integer, Integer>, String> *FmtB* = Map.*ofEntries*(

*entry*(new Pair<>(0, 0), "beq"),

*entry*(new Pair<>(0x1, 0), "bne"),

*entry*(new Pair<>(0x4, 0), "blt"),

*entry*(new Pair<>(0x5, 0), "bge"),

*entry*(new Pair<>(0x6, 0), "bltu"),

*entry*(new Pair<>(0x7, 0), "bgeu")

);

public static final Map<Integer, String> *registerNames* = Map.*ofEntries*(

*entry*(0, "zero"),

*entry*(1, "ra"),

*entry*(2, "sp"),

*entry*(3, "gp"),

*entry*(4, "tp"),

*entry*(5, "t0"),

*entry*(6, "t1"),

*entry*(7, "t2"),

*entry*(8, "s0 / fp"),

*entry*(9, "s1"),

*entry*(10, "a0"),

*entry*(11, "a1"),

*entry*(12, "a2"),

*entry*(13, "a3"),

*entry*(14, "a4"),

*entry*(15, "a5"),

*entry*(16, "a6"),

*entry*(17, "a7"),

*entry*(18, "s2"),

*entry*(19, "s3"),

*entry*(20, "s4"),

*entry*(21, "s5"),

*entry*(22, "s6"),

*entry*(23, "s7"),

*entry*(24, "s8"),



<a name="br26"></a> 

*entry*(25, "s9"),

*entry*(26, "s10"),

*entry*(27, "s11"),

*entry*(28, "t3"),

*entry*(29, "t4"),

*entry*(30, "t5"),

*entry*(31, "t6")

);

public enum FMT{

*RM*,

*I*,

*ILoad*,

*S*,

*B*,

*J*,

*IJalr*,

*ULui*,

*UAui*,

*IEnv*,

*unknown\_instruction*

};

public static long extractBytes(long offset, long byteNum) throws Exception {

if (byteNum <= 0 || byteNum > 4) {

throw new Exception("Wrong number of bytes to read in

extractBytes()");

}

long result = 0;

for(long i = 0; i < byteNum; i++){

if (offset + byteNum - i - 1 < 0 || offset + byteNum - i - 1 >= *TOTAL*)

{

throw new Exception("Index out of boundaries when trying to read

in extractBytes()");

}

result = result \* 256 + (*bytes*[(int) (offset + byteNum - i - 1)]);

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

**Pair.java**



<a name="br27"></a> 

import java.util.Objects;

public class Pair<F, S>{

private F first;

private S second;

public Pair(F first, S second) {

this.first = first;

this.second = second;

}

public void setFirst(F first) {

this.first = first;

}

public void setSecond(S second) {

this.second = second;

}

public F getFirst() {

return first;

}

public S getSecond() {

return second;

}

@Override

public boolean equals(Object o) {

if (this == o) return true;

if (o == null || getClass() != o.getClass()) return false;

Pair<?, ?> pair = (Pair<?, ?>) o;

return first.equals(pair.first) && second.equals(pair.second);

}

@Override

public int hashCode() {

return Objects.*hash*(first, second);

}

}

