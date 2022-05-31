## Información

Tomado de: https://www.youtube.com/watch?v=VVn9OG9nfH0&t=1069s

## Desde postman - Login (Venía por defecto)

```
[POST] http://localhost:8080/login
Body / x-www-form-urlencoded
username
password
```

## Desde postman - Login (ACTUAL)

```
[POST] http://localhost:8080/api/login
Body / x-www-form-urlencoded
username
password
```

Devolverá en el cuerpo de la respuesta un objeto Json
con el access_token y refresh_token.<br>
Nota: El path /login viene configurado por defecto por Spring.
Eso lo podemos ver en la clase <b>UsernamePasswordAuthenticationFilter</b>
del cual nuestra clase <b>CustomAuthenticationFilter</b> la hereda.

### Sobre escribiendo el path /login por /api/login

Por defecto el path /login viene en Spring. Lo que se hizo fue cambiar ese path
al siguiente <b>/api/login</b>, para eso en la clase <b>SecurityConfig</b> método
configure, se agrega lo siguiente:

```
@Override
    protected void configure(HttpSecurity http) throws Exception {
    //Creando nuestra propio path de login que por defecto en Spring es /login
    CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(this.authenticationManager());
    customAuthenticationFilter.setFilterProcessesUrl("/api/login");
    ......
    ...
```
Luego, debemos asegurarnos de que la seguridad no bloquee ese nuevo path
```
//Configuración de la seguridad Global del sistema
@Override
protected void configure(HttpSecurity http) throws Exception {
    //Creando nuestra propio path de login que por defecto en Spring es /login
    CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(this.authenticationManager());
    customAuthenticationFilter.setFilterProcessesUrl("/api/login");


    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests().antMatchers("/api/login/**").permitAll(); <<<<<<<<<<--------------------------------
    http.authorizeRequests().antMatchers(HttpMethod.GET, "/api/users/**").hasAnyAuthority("ROLE_USER");
    http.authorizeRequests().antMatchers(HttpMethod.POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN");
    http.authorizeRequests().anyRequest().authenticated(); 

    http.addFilter(customAuthenticationFilter);
}
```
<b>NOTA: </b>El orden en que son colocados las URLs de autorización SÍ IMPORTA