precision mediump float;
varying vec2 v_texPo;
uniform sampler2D s_Texture;
uniform float c_alpha;
void main(){
    vec4 argb=texture2D(s_Texture,v_texPo);
    argb.a=c_alpha;
    gl_FragColor =argb;
}
