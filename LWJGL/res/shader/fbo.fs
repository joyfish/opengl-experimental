const mat3 kernel = mat3(1.0, 2.0, 1.0,
						2.0, 4.0, 2.0,
						1.0, 2.0, 1.0) / 16.0;
const vec4 fog = vec4(0.2, 0.2, 0.7, 1.0);
const vec2 vector = vec2(0.001);

uniform sampler2D texture, depth;
uniform float time;

varying vec3 normal;
varying vec4 vertex;
varying vec2 texCoord;

float linearize(float z) {
	float n = 0.001, f = 4.0;
	return (n * z) / ( f - z * (f - n) );
}

vec2 shift(vec2 texCoord) {
    return texCoord;
}

void main() {
    vec2 newTexCoord = clamp(shift(texCoord), 0.0, 1.0);
    vec4 color = vec4(0.0);
    for(int x = 0; x < 3; x++) {
        for(int y = 0; y < 3; y++) {
          color += kernel[x][y] * texture(texture, clamp(newTexCoord + vector * vec2(x, y), 0.0, 1.0));
        }
    }
    gl_FragColor = mix(color, fog, linearize(texture(depth, newTexCoord).r));
}