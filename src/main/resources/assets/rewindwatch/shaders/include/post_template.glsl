#version 150

vec4 transformColor(vec4 color, vec2 coord);

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

out vec4 fragColor;

void main(){
    vec4 color = transformColor(texture(DiffuseSampler, texCoord), texCoord);
    fragColor = vec4(color.rgb, 1.0);
}
