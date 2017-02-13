Active TCL

����Ƕ������Ӧ�ó���
���Դ����쳣



����ָ���";" ���߿ո�
\ ���Է���ɶ�������
\x ʮ������

\[ ת��

��Сд�����е�
ע��ʹ�� #

ʹ�ñ���Ҫ��$

set iNum2 43; set iNum3 44  
set sMyvar "Hard Disk"
puts $sMyvar
#��� => Hard Disk

set sAreaCode (213)
puts ${sAreaCode}555-1212
#==> (213)555-1212

[] �е�tcl����ִ��,���ؽ�������[]
set iResult [ expr 2 + 2 ]


set iNum 99
puts "The value of iNum is $iNum"    
Ҫ����������
#==> The value of iNum is 99

puts "Some text; and
more text"
#==> Some text; and
#==> more text

set iCount 99
puts {The value of iCount is $iCount}   
������������
#==> The value of iCount is $iCount

puts {hello \n hello; $more [text]}
==>hello \n hello; $more [text]
 


set iSize 2
incr iSize 2
puts $iSize
==>4


#append varName value ?value
append sPartName Memory " " 128 MB
puts $sPartName
==>Memory 128MB


#The iSize and sName variables are deleted
unset iSize sName

expr (10/2) + 3
==>8


format
regexp
regsub
scan
string compare
string first
string index
string last
string length
string match
string range
string tolower
string toupper
string trim
string trimleft
string trimright
 
 
string length "A test string"
==>13
 
string index "Hello World" 4
==>o
#��4������ĸ,0��ʼ

string range "A test string" 7 end
==>string
#�ӵ�7������β

string first "!" "Help!Me!"
==>4
#��һ��!��λ��

string last "!" "Help!Me!"
==>7

string compare "First" "Second"
==>-1
# -1, 0,  1 �ֱ��ʾ��һ��С��,����,���ڵڶ���

string match "*test*" "A test string"
==>1
#0��ʾ��ƥ��

string tolower "A Test String"
==>a test string

string toupper "A Test String"
==>A TEST STRING

string trim " A test string !!!!" " !"
==>A test string
#ɾ�׺�β���� !,Ĭ���ǿո�

string trimleft " A test string !!!!" " !"
==>A test string !!!!

string trimright " A test string !!!!" " !"
==> A test string

regexp "does|match$" "Does this match"
==>1
#0 ��ʾ��ƥ��

#regsub exp 	string 							substitute  	varName
regsub "match$" "match test - Does this match"  "**replaced**" sStr
==>1
puts $sStr
==> match test - Does this **replaced**

format "The %s number %d is %x in %s" \
decimal 23 23 hex
==>The decimal number 23 is 17 in hex

scan "Decimal 23 = 17" "%s %d = %x" a b c
==>3
#Variable a=Decimal, b=23 and c=23. Returns number of scanned  items (3).



set lComps [list Memory Mouse "System Box"]
==>Memory Mouse {System Box}

set lComps [concat $lComps [list Monitor Keyboard]]
==>Memory Mouse {System Box} Monitor Keyboard

#lappend varName value ?value
lappend lComps Monitor
==> Memory Mouse {System Box} Monitor

lindex $lComps 2
==>System Box

llength $lComps
==>5

lrange $lComps 0 2
==> Memory Mouse {System Box}

linsert $lComps 2 RAM
==> Memory Mouse RAM {System Box} Monitor Keyboard

#lreplace list first last ?value value ��?
lreplace $lComps 3 3
==> Memory Mouse RAM Monitor Keyboard
#Item 3 of list lComps is replaced with nothing


#Returns the index of the first item in list(-1 if there are no matches)
lsearch -glob $lComps *r*
==>0
#which is Memory, item 0

#Options include -ascii, -integer, -real, -dictionary, -increasing, -decreasing, -index x, -command cmd .
# -real���б�Ԫ��ת���ɸ�����
# -command ʹ��command��Ϊһ���Ƚ�����Ƚ�����Ԫ�ء����������Ҫ����һ����������ʾС�ڡ����ڻ����0���ֱ��Ӧ��һ��Ԫ����С�ڡ����ڻ��Ǵ��ڵڶ���Ԫ�ء� 
	
lsort -ascii $lComps
==> Keyboard Memory Monitor Mouse RAM

lsort -integer -index 1 /
		{{First 24} {Second 18} {Third 30}}

���� {Second 18} {First 24} {Third 30}, 


lsort -index end-1  {{a 1 e i} {b 2 3 f g} {c 4 5 6 d h}}
���� {c 4 5 6 d h} {a 1 e i} {b 2 3 f g},


#�Ƚ϶�ά�е�0,1 ,��i,e,o
lsort -index {0 1} {
       {{b i g} 12345}
       {{d e m o} 34512}
       {{c o d e} 54321}
    }
���� {{d e m o} 34512} {{b i g} 12345} {{c o d e} 54321} 

split [list a b c] \n

join $lGroups "|"
==>HCC Corp|Sales|Manufacturing


����,�����������ִ�,��Map
set aPerson(firstname) Tom
#The array aPerson is created

puts $aPerson(firstname)
=> Tom


array exists
array size
array names
array set
array get

array exists aPerson
==>1

array size aPerson
==>2

array names aPerson
==>firstname lastname

#��list����
array set aPerson {name Joe job Boss}
#Creates array aPerson and associates name with Joe and job with Boss

array get aPerson
==>name Joe job Boss

#TCL �ڲ�������� env,
array get env

#TCL �ڲ�������� �ͻ���,��tcl_ ��ͷ
array get tcl_platform

#info keyword arg��
info vars s*
info exists sPart
info procs


break
continue
for
foreach
if
switch
while

# }{ ������һ����
if { $sPart == "Memory" } {
puts "Found Memory"
} elseif {$sPart == "Mouse"} {
puts "Found Mouse"
} else {
puts "Not Found"
}
#ע��{��λ��

#Options include -exact, -glob, -regexp
switch ?options? string {
value body
?default body ��?
}

switch -exact $sType {
Memory {mql print Type $sType }
"System Box" -
Microcomputer {mql delete Type $sType}
CustOrder {mql icon type $sType verbose}
default {puts "Undefined Type" }
}

for {set iNum 0} {$iNum<3} {incr iNum} {
puts $iNum
}
=> 012

#while test body
while { $iNum < 10 } { incr iNum }



foreach sRole [split [mql list role] \n] {
puts \
[mql print role $sRole select name person dump]
}

#eval ����
set sType "Monitor"
set cmd {mql print bus $sType select policy}
eval $cmd
==>Production

source file.tcl


catch { mql print type Mousx } sMessage
==>1
puts $sMessage
==>Error: #1900068: print type failed


errorInfo��errorCodeֻ�ڴ����쳣�б�����

error message ?info? ?code?

Generates an error with the specified error message, aborts the
current Tcl script, and places info into the errorInfo variable and
code into the errorCode variable



Procedures �����û��Զ�������
ʹ�� "proc" ���� ,����ֻҪ ����
global
proc
return
upvar


�﷨
proc name argList body

proc pMyprint { sText } { puts $sText }


#�ɶԲ�������Ĭ��ֵ
proc pAddOrder { sType sName sRev { sPol "Order" } } {
mql add bus $sType $sName $sRev \
policy $sPol
}


#args ���Եõ���������
proc pNotify { sSubj sMsg args } {
foreach sPerson $args {
mql send mail to $sPerson subject $sSubj \
text $sMsg
}
}


����
pNotify "Meeting" "Room B - 2:00 PM" Ni Joe


proc getPersonsFromRole { sRole } {
set lPersons \
[mql print role $sRole select person dump]
return $lPersons
}

proc �еı����Ǿֲ�����

global iPersons  ȫ�ֱ���

proc getListLength { sList } {
global iPersons
set iPersons [llength $sList]
return $iPersons
}
set iPersons 0
getListLength "Joe Mary Ni"
==>3
puts $iPersons
==>3

upvar ��������,����������

upvar sVarName1 myVarName1
?sVarName2 myVarName2 ��?


set Memory(RAM) 100
set Memory(Price) 100
proc PrintArrayElement { aArrayVar sElement} {
upvar $aArrayVar aLocalArray
puts $aLocalArray($sElement)
}
PrintArrayElement Memory Price
==> 100

#���ļ�,��ֻ����ʽ,�����ļ�ID
set fInput [open "file.tcl" r]
==>file64

#���ļ���һ��(15 bytes)
gets $fInput sText
==>15




Reads and returns the next numBytes bytes of data in fileId
read fileId numBytes
read $fInput 50


read ?-nonewline? fileId 
��������fileId��ʶ���ļ�������ʣ�µ��ֽڡ����û��nonewline���أ����ڻ��з���ֹͣ�� 

eof fileId ,����1,0
tell $fInput
==>199
current access position is at byte 199


seek fileId offset ?origin?
Origin may be start, current, or end, and defaults to start
seek $fInput 50 start


#���ļ���д
puts $fOutput "This is a test"


flush fileId
close fileId


file atime
file copy
file delete
file dirname
file executable
file exists
file extension
file isdirectory
file isfile
file join
file lstat
file mkdir
file mtime
file owned
file readable
file readlink
file rename
file rootname
file size
file split
file stat
file tail
file type
file writable



file stat file.tcl aFileInfo
array get aFileInfo
==>mtime 853450007 atime 853945190 gid 0
nlink 1 mode -32320 type file
ctime 853450007 uid 0 ino 0
size 21932 dev 2

��ʽ������,ʱ��
clock format timevalue


glob pattern - Return a list of filenames
glob *.bat


�����ⲿ���� ,֧��IO �ض���

exec
exit
open
pid

exec notepad &
























