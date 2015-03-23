uniform float time;

varying vec3 normal;
varying vec4 vertex;
varying vec2 texCoord;

void main() {
    normal = gl_Normal;
    vertex = gl_Vertex;
    gl_Position = gl_ModelViewProjectionMatrix * vertex;
    texCoord = gl_MultiTexCoord0.xy;
}