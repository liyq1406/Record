-----
eclipse marketplace��װ  Python ��� PyDev-5.4 ,��װʱ������ʾ�汾����,��ʹ��ʱ��ʾҪeclipse-4.6 ����

�ٷ����汾��Ӧ��ϵ(Eclipse 4.5, Java 8: PyDev 5.2.0)
����PyDev���߰�װ�� 
https://sourceforge.net/projects/pydev/files/pydev/  ����dropsin��װ

����debug 

���Կ���Jython,IronPython
Jython : Python for the Java Platform
IronPython:��Դʵ��Python����.Net Framework

preferences->PyDev->Interpreters->Python Interpreter->ѡ��python.exe����·��->��ʾstdlib source�Ҳ���
������ʾ�ܺ�
shift+enter �Զ��������� ,ctrl+1�кܶ๦��
-----


Python-3.5.2  ����win .zip ��ѹ,��PATH����������,û��Դ��,û��Doc

�������.exe��װ��,.py�ļ�����˫��ִ��
��װ�汾Ĭ�ϰ�װ�� C:\Users\zhaojin\AppData\Local\Programs\Python\Python35  ��.chm�ĵ�,��LibĿ¼��Դ��


���� python test.py  ���ļ�����ҪΪUTF-8�ı������

����
python -O -m py_compile test.py    ������__pycache__Ŀ¼����.pyc�������ļ�,���Ա�������, python .pyc ��ִ��
-O    optimize

Ҳ��д�ű������� comiple.py , ���˫��.py�ļ�ִ����ֻ������ͬĿ¼����test.pyc�ļ�
import py_compile
print('begin compile')
py_compile.compile('c:/tmp/test.py')    
print('end compile')

�������� ��������__pycache__Ŀ¼
import compileall
compileall.compile_dir(r'c:\tmp\pyDir')

����  python -m compileall c:\tmp\pyDir


����  _  ��ʾ�ϴδ�ӡ������

>>>2*5
��ʾ 10
>>>j=2
>>>j+_  #��ʾ12,_��10
>>> print(3)  #��������浽 _ ��


>>> word = 'Python'
>>> word[0:2]   #���ִ�
>>> word[:2] + word[2:]   # Py   thon
>>> len(word)
>>> word[-2:]  #��ʾ on



>>> letters = ['a', 'b', 'c', 'd', 'e', 'f', 'g']
>>> #�滻
>>> letters[2:5] = ['C', 'D', 'E']
>>> letters
>>> ['a', 'b', 'C', 'D', 'E', 'f', 'g']
>>> letters[2:5] = [] #ɾ��,��List,�������м�յ�
>>> letters
['a', 'b', 'f', 'g']
>>> letters[:]  #ȫ����
>>> a = ['a', 'b', 'c']
>>> n = [1, 2, 3]
>>> x = [a, n]
>>> x
[['a', 'b', 'c'], [1, 2, 3]]
>>> i = 256*256
>>> print('The value of i is', i)

>>>2**3   #��ʾ��2��3�η�
 
>>> a, b = 0, 1  #ͬa=0 �� b=1
>>>while b < 10: #����������
    print(b, end=',') #ǰ��һ��Ҫ��tab(���ǲ��ܱ�������)���߿ո�,����Ĵ��붼Ҫ�����е�����һ��
    a, b = b, a+b   #a��b ͬʱ��ֵ= �����, ���൱��a=b,��a����ԭ����a,b=a+b
#����ѭ��һ���س�

#��� 1,1,2,3,5,8, 

#��1��ִ��
1
#a=b=1    b=a+b=0+1=1    #��b��ֵʱʹ�õ�a������ֵ�����и�a�µ�ֵ����������ʹ��

#��2��ִ��
1
#a=b=1   b=a+b=1+1=2

 
x = int(input("Please enter an integer: "))
����ʾ Please enter an integer: 42
if x < 0:
  x = 0
  print('Negative changed to zero')
elif x == 0:
  print('Zero')
elif x == 1:
  print('Single')
else:
  print('More')


words = ['cat', 'window', 'defenestrate']
for w in words:  
#for w in words[:]: 
  print(w, len(w))

for i in range(5): 
   print(i)

range(5, 10)   ��ʾ�� 5 �� 9
range(0, 10, 3) ��ʾ�� 0 �� 9 ����Ϊ3
print(range(10))  #ͬrange(0,10)


list(range(5))


10//3 ȡ������

while True:
   pass  # Busy-wait for keyboard interrupt (Ctrl+C)


def ask_ok(prompt, retries=4, reminder='Please try again!'):
    while True:
        ok = input(prompt)
        if ok in ('y', 'ye', 'yes'):
            return True
        if ok in ('n', 'no', 'nop', 'nope'):
            return False
        retries = retries - 1
        if retries < 0:
            raise ValueError('invalid user response')
        print(reminder)
        
#ask_ok('accept deault ?',3)

print('\n')
i = 5

def f(arg=i): #��ʹ��Ĭ��ֵ,ֻ��ʹ��ǰ���,����ʹ�ú�ͬ��
    print(arg)

i = 6
f()  #���5

#-------------
'''    #����ע��
def f(a, L=[]):  #Ĭ��ֵֻ��һ�γ�ʼ��
    L.append(a)
    return L

print(f(1))
print(f(2))
print(f(3))

'''
#-------------
def f(a, L=None):
    if L is None:
        L = []
    L.append(a)
    return L






