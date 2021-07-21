echo off
echo "$@"
PARAM_INPUT="$@"
GIT_COMMAND=${PARAM_INPUT}
echo GIT_COMMAND=${GIT_COMMAND}
${GIT_COMMAND}
${GIT_COMMAND} >>/home/jenkins/plugins/mscodecoverage-plugin/git_command_out.txt
