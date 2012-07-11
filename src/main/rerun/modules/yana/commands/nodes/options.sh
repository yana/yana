# generated by stubbs:add-option
# Wed May 23 08:08:36 PDT 2012

# print USAGE and exit
rerun_option_error() {
    [ -z "$USAGE"  ] && echo "$USAGE" >&2
    [ -z "$SYNTAX" ] && echo "$SYNTAX $*" >&2
    return 2
}

# check option has its argument
rerun_option_check() {
    [ "$1" -lt 2 ] && rerun_option_error
}

# options: [type cfg]
while [ "$#" -gt 0 ]; do
    OPT="$1"
    case "$OPT" in
          -t|--type) rerun_option_check $# ; TYPE=$2 ; shift ;;
          -n|--name) rerun_option_check $# ; NAME=$2 ; shift ;;
          -F|--format) rerun_option_check $# ; FORMAT=$2 ; shift ;;
  -C|--cfg) rerun_option_check $# ; CFG=$2 ; shift ;;
        # unknown option
        -?)
            rerun_option_error
            ;;
        # end of options, just arguments left
        *)
          break
    esac
    shift
done

# If defaultable options variables are unset, set them to their DEFAULT
[ -z "$CFG" ] && CFG="$HOME/.yanarc"
[ -z "$FORMAT" ] && FORMAT='${ID}:${NAME}:${TYPE}:${DESCRIPTION}'

# Check required options are set
[ -z "$CFG" ] && { echo "missing required option: --cfg" ; return 2 ; }
#
return 0
