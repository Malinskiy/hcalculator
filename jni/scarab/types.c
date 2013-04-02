/*
 *  types.c
 *  integer-fhe
 *
 *  Created by Henning Perl on 25.11.10.
 *  Copyright 2010 Henning Perl. All rights reserved.
 *
 */

#include "types.h"


/** memory management **/

void fhe_pk_init(fhe_pk_t pk)
{
	mpz_init(pk->p);
	mpz_init(pk->alpha);
	for (int i = 0; i < S1; i++) {
		mpz_init(pk->B[i]);
		mpz_init(pk->c[i]);
	}
}


void fhe_pk_clear(fhe_pk_t pk)
{
	mpz_clear(pk->p);
	mpz_clear(pk->alpha);
	for (int i = 0; i < S1; i++) {
		mpz_clear(pk->B[i]);
		mpz_clear(pk->c[i]);
	}
}


void fhe_sk_init(fhe_sk_t sk)
{
	mpz_init(sk->p);
	mpz_init(sk->B);
}


void fhe_sk_clear(fhe_sk_t sk)
{
	mpz_clear(sk->p);
	mpz_clear(sk->B);
}

/** output **/

void fhe_pk_print(fhe_pk_t pk)
{
	printf("public key:\n");
	gmp_printf("\tp  =\t%Zd\n", pk->p);
	gmp_printf("\tα  =\t%Zd\n", pk->alpha);
	printf("\tc[i]\tB[i]\n");
	for (int i = 0; i < S1; i++) {
		gmp_printf("\t%Zd\n\t\t%Zd\n", pk->c[i], pk->B[i]);
	}
}

void fhe_pk_print_mathematica(fhe_pk_t pk)
{
	printf("public key:\n");
	gmp_printf("{%Zd, %Zd, %i, %i, {{", pk->p, pk->alpha, S1, S2);
	for (int i = 0; i < S1; i++) {
		gmp_printf("%Zd", pk->c[i]);
		if (i < S1-1) {
			printf(", ");
		}
	}
	printf("}, {");
	for (int i = 0; i < S1; i++) {
		gmp_printf("%Zd", pk->B[i]);
		if (i < S1-1) {
			printf(", ");
		}	}
	printf("}}}\n");
}

void fhe_sk_print(fhe_sk_t sk)
{
	printf("secret key:\n");
	gmp_printf("\tp =\t%Zd\n", sk->p);
	gmp_printf("\tB =\t%Zd\n", sk->B);
}

void fhe_sk_print_mathematica(fhe_sk_t sk)
{
	printf("secret key:\n");
	gmp_printf("{%Zd, %Zd}\n", sk->p, sk->B);
}


char * fhe_pk_to_str(fhe_pk_t pk)
{
    char* str = (char*) malloc(sizeof (char) * 65536);
    char* buf = (char*) malloc(sizeof (char) * 65536);
    if (str == NULL || buf == NULL) {
        if(buf != NULL)
            free(buf);
        if(str != NULL)
            free(str);
        return NULL;
    }
    strcpy(str, "");
    strcat(str, "\tp  =\t");

    mpz_get_str(buf, 10, pk->p);
    strcat(str, buf);

    strcat(str, "\n");
    strcat(str, "\tα  =\t");

    mpz_get_str(buf, 10, pk->alpha);
    strcat(str, buf);

    strcat(str, "\n");
    strcat(str, "\tc[i]\tB[i]\t\n");
    for (int i = 0; i < S1; i++) {
        strcat(str, "\t");

        mpz_get_str(buf, 10, pk->c[i]);
        strcat(str, buf);
        strcat(str, "\t\t");

        mpz_get_str(buf, 10, pk->B[i]);
        strcat(str, buf);
    }
    strcat(str, "EOK");
    free(buf);
    return str;
}

char * fhe_sk_to_str(fhe_sk_t sk)
{
	char* str = (char*) malloc(sizeof (char) * 65536);
    char* buf = (char*) malloc(sizeof (char) * 65536);
    if (str == NULL || buf == NULL) {
        if(buf != NULL)
            free(buf);
        if(str != NULL)
            free(str);
        return NULL;
    }
    strcpy(str, "");
    strcat(str, "\tp  =\t");

    mpz_get_str(buf, 10, sk->p);
    strcat(str, buf);

    strcat(str, "\n");
    strcat(str, "\tB  =\t");

    mpz_get_str(buf, 10, sk->B);

    strcat(str, buf);
    strcat(str, "\nEOK");
    free(buf);

    return str;
}