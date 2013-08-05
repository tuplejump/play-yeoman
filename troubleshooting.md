Troubleshooting
---------------

### Dist in angular JS messes up the styles

This is a known issue in the yeoman generated Angular JS project GruntFile. This is because of the concat action messing up with the stylesheets.
Should be fixed in newer versions, you can use the solutions. The build task in Gruntfile.js looks like this -

```javascript
  grunt.registerTask('build', [
    'clean:dist',
    'jshint',
    'test',
    'coffee',
    'compass:dist',
    'useminPrepare',
    'imagemin',
    'cssmin',
    'htmlmin',
    'concat',
    'copy',
    'cdnify',
    'ngmin',
    'uglify',
    'rev',
    'usemin'
  ]);
```

Move the concat task to be just below *useminPrepare*

```javascript
  grunt.registerTask('build', [
    'clean:dist',
    'jshint',
    'test',
    'coffee',
    'compass:dist',
    'useminPrepare',
    'concat',
    'imagemin',
    'cssmin',
    'htmlmin',
    'copy',
    'cdnify',
    'ngmin',
    'uglify',
    'rev',
    'usemin'
  ]);
```


### Icons from bootstrap don't show up
Again an issue with Angular JS generator when using non standard locations. For this you would need to change the http base for images.

The original compass task looks like this -

```javascript
    compass: {
      options: {
        sassDir: '<%= yeoman.app %>/styles',
        cssDir: '.tmp/styles',
        imagesDir: '<%= yeoman.app %>/images',
        javascriptsDir: '<%= yeoman.app %>/scripts',
        fontsDir: '<%= yeoman.app %>/styles/fonts',
        importPath: '<%= yeoman.app %>/components',
        relativeAssets: true
      },
      dist: {},
      server: {
        options: {
          debugInfo: true
        }
      }
    },
```

Change relativeAssets to false and add raw config for http images path, so it will be like this

```javascript
    compass: {
      options: {
        sassDir: '<%= yeoman.app %>/styles',
        cssDir: '.tmp/styles',
        imagesDir: '<%= yeoman.app %>/images',
        javascriptsDir: '<%= yeoman.app %>/scripts',
        fontsDir: '<%= yeoman.app %>/styles/fonts',
        importPath: '<%= yeoman.app %>/components',
        relativeAssets: false,
        raw: 'http_images_path = \'../images\'\nhttp_generated_images_path = \'../images\'\n'
      },
      dist: {},
      server: {
        options: {
          debugInfo: true
        }
      }
    },
```


