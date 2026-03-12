# Reglas de ProGuard para la app de la Calculadora.
# ProGuard/R8 elimina código no usado y ofusca el código en builds de release.
-keep class com.calculadora.** { *; }
