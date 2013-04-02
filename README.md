HCalculator
==============

Annotation
--------------------
This project is a simple calculator application that uses homomorphic encryption scheme implemented in scarab library.
It's based on following papers:
C. Gentry, A fully homomorphic encryption scheme
N. Smart and F. Vercauteren, Fully Homomorphic Encryption with Relatively Small Key and Ciphertext Sizes
H. Perl, M. Brenner and M. Smith, POSTER: An Implementation of the Fully Homomorphic Smart-Vercauteren Crypto-System

Components
--------------------
GMP 5.1.1, MPFR 3.1.2, FLINT 1.6 and SCARAB 1.0.0 are the libraries that I used to implement this application.
I've compiled them for armeabi, armeabi-v7a and x86 architectures for android with android-ndk-r8d standard toolchains.
If you want to recompile them from source there is a make.sh script in jni directory that downloads all the necessary
files and patches if necessary. Be warned: there is no error checking in the script so be sure to check the env_export
to give the script all the necessary details about where your NDK and toolchains are placed.
The Scarab class is used to access JNI methods. Scarab library implements functions that work on 1-bit block.
These function include XOR and AND. I used these and 1-const to form basis for all other boolean functions according
with Post's theorem.

Application
--------------------
The application itself is based on Android's Calculator app. The sources were taken from AOSP project.
When you start the application the key generation method is invoked. The keys are stored in native code, but you can get
them with getters from Scarab class. If you by chance interrupt the key generation (with back button for example) the
application will most likely crash, so don't interrupt this procedure :-)
After that you've got the basic calculator layout and +/- operations.
All the operands are 32-bit buy default but you can change that inside the HInteger class. Be warned that there is no
overflow correction.
The basic boolean operation takes about 300ms on Galaxy Nexus 4 with NEON optimizations. 32-bit addition takes about 1
minute and subtraction takes about 2 minutes.
You can also test scarab integrity by using options menu. This executes a series of test on the mobile device itself. Currently it's a series
of XOR, AND, ADD and SUB operations. After that you get the results table.

License
--------------------
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.