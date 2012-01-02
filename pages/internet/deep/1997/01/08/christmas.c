/*
 * christmas.c 
 * a program that prints out the twelve days of chrismas
 *   in clear and obvious fashion
 *
 * adopted from the obfuscated version by Michael Bayne <mdb@go2net.com>
 * and Paul Phillips <paulp@go2net.com>
 */

#include <stdio.h>

#define babble1 "@n'+,#'/*{}w+/w#cdnr/+,{}r/*de}+,/*{*+,/w{%+,/w#q#n+,/#{l+,/n{n+,/+#n+,/#;#q#n+,/+k#;*+,/'r :'d*'3,}{w+K w'K:'+}e#';dq#'l q#'+d'K#!/+k#;q#'r}eKK#}w'r}eKK{nl]'/#;#q#n'){)#}w'){){nl]'/+#n';d}rw' i;# ){nl]!/n{n#'; r{#w'r nc{nl]'/#{l,+'K {rw' iK{;[{nl]'/w#q#n'wk nw' iwk{KK{nl]!/w{%'l##w#' i; :{nl]'/*{q#'ld;r'}{nlwb!/*de}'c ;;{nl'-{}rw]'/+,}##'*}#nc,',#nw]'/+kd'+e}+;#'rdq#w! nr'/ ') }+}{rl#'{n' ')#}'+}##(!!/"

#define babble2 "!ek;dc i@bK'(q)-[w]*%n+r3#l,{}:\nuwloca-O;m .vpbks,fxntdCeghiry"

int main (int arg1, int arg2, char* arg3)
{
    /* if (!0 < arg1) { */
    if (arg1 > 1) {
        int rv;

        /* arg1 == 2, start printing day #arg2 */
        /* if (arg1 < 3) { */
        if (arg1 == 2) {
            int rv1, rv2;

            /* prints out 'On the ' */
            /* arg3 ignored, results in call to main(0, -86, babble1) */
            rv1 = main(-86, 0, arg3 + 1);

            /* prints out the ordinal date (ie. tenth) */
            /* arg3 ignored, results in call to main(1-day, -87, babble1) */
            rv2 = main(-87, 1 - arg2, rv1 + arg3);

            /* prints out 'day of Christmas my true love gave to me' */
            /* arg3 ignored, results in call to main(-13, -79, babble1) */
            main(-79, -13, arg3 + rv2);
        } /* else 1; */

        /* here we stack up a bunch of calls to ourself in 'phrase printing'
           mode to print out each successive phrase (partridge in a pear tree,
           and, two turtle doves). each call prints out the successively
           previous phrase, and we keep recursing until we've made one call
           for each day */
        if (arg1 < arg2) {
            main(arg1 + 1, arg2, arg3);
        } /* else 3; */

        /* this call causes us to print out the (-27 + arg1) th phrase in the
           phrase buffer, there are 26 phrases, so arg1 varies from 2 to 13 in
           order to print out each of the day's' gifts */
        /* arg3 ignored, results in call to main(-27 + arg1, -94, babble1) */

        rv = main(-94, -27 + arg1, arg3);

        /* rv is always 16 (hence true). see below where we return 16 instead
           of advancing to the next day */

        /* we don't execute this on the stacked up recursive calls made above
           (where we pass in arg1 values from 3 to the number of the current
           day), but only on the primary call (where we passed in 2), because
           this code causes us to write out the next day */
        if (rv && (arg1 == 2)) {

            /* here's where we decide how many days to print */
            if (arg2 < 13) {

                /* 2 means we're a main recursive call that does a new day
                   arg2 + 1 increments the day
                   "%s %d %d" is to confuse us (it's ignored) */
                return main(2, arg2 + 1, "%s %d %d\n");

            } else {
                /* this is the return value of the command  --
                   echo $status after running this :-) */
                return 9;
            }

        } else {
            /* this is the return value of the above described recursive
               calls, must be non-zero in original code because its return
               value is checked */
            return 16;
        }

    } else {
        if (arg1 < 0) {

            if (arg1 < -72) {
                /* this is the stealthy switch of the first two arguments and
                   replacement of argument 3 with a pointer to the word
                   buffer */
                return main(arg2, arg1, babble1);

            } else {
                /* if we're here, we want to translate and print out a
                   character */
                if (arg1 < -50) {

                    /* if arg2 (the untranslated character we want to print)
                       is equal to *arg3 (some character in the translation
                       buffer */
                    if (arg2 == *arg3) {

                        /* if so, we're all set, print out the translated
                           version of the character (which is 31 characters up
                           the translation table) */
                        return putchar(arg3[31]);

                    } else {
                        /* if not, call ourselves with the printout magic
                           number, the same untranslated character and the
                           next character up in the translation table */
                        return main(-65, arg2, arg3 + 1);
                    }

                /* if we're here, we want to print out a phrase. we start
                   out with arg3 pointing to babble1, and arg1 being -(the
                   number of phrases to skip before printing out a
                   phrase), we increment the pointer, incrementing arg1
                   each time we pass a slash, so when arg1 equals zero,
                   we've skipped the appropriate number of slashes
                   (phrases) and we end up down in the phrase printing
                   part of the code */
                } else {
                    /* pushes babble1 pointer forward (skipping slashes) and
                       incrementing arg1 (every time a slash is seen) until
                       arg1 == 0. this positions a pointer in the babble2
                       buffer at the beginning of the -arg1 th
                       (slash-seperated) string */
                    return main((*arg3 == '/') + arg1, arg2, arg3+1);
                }                    
            }

        } else {
            /* if (0 < arg1) { */
            if (arg1 == 1) {

                /* this is the original call, since we are originally called
                   with arg1 == 1 (argc) */
                return main(2, 2, "%s");

            } else { /* arg1 == 0 */
                /* we've got a correctly positioned pointer into babble2 in
                   arg3  */
                if (*arg3 == '/') {

                    /* we've hit the end of the phrase that we wanted to
                       translate and print */
                    return 1;

                } else {
                    /* -61 could be any number greater than -72 but less than
                       -50 to cause us to go into printing out mode
                       *arg2 is a pointer to the untranslated character that we
                       want to print */
                    int rv = main(-61, *arg3, babble2);

                    /* we call ourselves again with the next character in the
                       word buffer, we'll end up right back here until we hit
                       a '/' at which point, we'll return (1) all the way up */
                    return main(0, rv, arg3+1);
                }
            }
        }
    }
}

