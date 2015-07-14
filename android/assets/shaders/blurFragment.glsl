//#ifdef GL_ES
//#define LOWP lowp
//precision mediump float;
//#else
//#define LOWP
//#endif

varying vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;
uniform float bias;

void main() {
    vec4 texColor = texture2D(u_texture, vTexCoord, bias);
    gl_FragColor = texColor * vColor;

}