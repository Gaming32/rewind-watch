#version 150

#moj_import <rewindwatch:rendertype_entity_translucent_template.glsl>
#moj_import <rewindwatch:dissolve.glsl>
#moj_import <rewindwatch:grayscale.glsl>

uniform float DissolveOpacity;

vec4 transformColor(vec4 color, vec2 coord) {
    if (DissolveOpacity < 0) {
        return shouldDissolve(coord, DissolveOpacity + 1) ? vec4(0) : grayscale(color);
    }
    return shouldDissolve(coord, DissolveOpacity) ? grayscale(color) : color;
}
