attribute vec4 av_Position;
uniform float p_Size;
void main(){
    gl_Position = av_Position;
    gl_PointSize = p_Size;
}
