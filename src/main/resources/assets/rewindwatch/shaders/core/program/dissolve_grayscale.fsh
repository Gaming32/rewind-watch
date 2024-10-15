#version 150

#moj_import <rewindwatch:post_template.glsl>
#moj_import <rewindwatch:dissolve.glsl>
#moj_import <rewindwatch:grayscale.glsl>

uniform float DissolveOpacity;

vec4 transformColor(vec4 color, vec2 coord) {
    return shouldDissolve(coord, DissolveOpacity) ? grayscale(color) : color;
}
