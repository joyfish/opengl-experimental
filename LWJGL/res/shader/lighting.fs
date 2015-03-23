const vec3 mapDir = vec3(0.0, 0.0, -1.0);
const vec4 color = vec4(1.0);

uniform vec3 lightPosition;
uniform sampler2D texture, normalMap;

varying vec3 normal;
varying vec4 vertex;
varying vec2 texCoord;

vec3 tangent(vec3 v, vec3 normal, vec3 ref) {
	vec3 axis = cross(normal, ref);
    float cosine = dot(normal, ref), sine = sqrt(1.0 - cosine * cosine);
    return cosine * v + sine * cross(v, axis) + ((1 - cosine) * dot(v, axis) * axis);
}

vec3 getMapNormal(sampler2D texture, vec2 texCoord) {
    return normalize(2.0 * texture(texture, texCoord).rgb - 1.0);
}

void main() {
    vec3 position = (gl_ModelViewMatrix * vertex).xyz;
    vec3 glNormal = normalize((gl_NormalMatrix * normal).xyz);
    vec3 mapNormal = getMapNormal(normalMap, 20.0 * texCoord);
    vec3 surfaceNormal = tangent(mapNormal, glNormal, mapDir);
    vec3 lightDir = normalize(position - lightPosition);
    float light = pow(dot(surfaceNormal, lightDir), 12.0);
    gl_FragColor = light * texture(texture, 20.0 * texCoord);
}