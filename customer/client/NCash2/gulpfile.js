var gulp = require('gulp');
//var sourcemaps = require('gulp-sourcemaps');
var mkdirp = require('mkdirp');
var gutil = require('gulp-util');
var watch = require('gulp-watch');
var del = require('del');
var concat = require('gulp-concat');
var uglify_js = require('gulp-uglify');
var minify_scss = require('gulp-sass');
var clean_css = require('gulp-clean-css');
var minify_img = require('gulp-imagemin');

gulp.task('serve:before', ['default']);

gulp.task('default', ['del', 'make-dir', 'uglify-js', 'minify-css', 'minify-scss', 'image-min'], function(){
    gutil.log('Running Gulp Tasks');
});

gulp.task('del', function() {
    gutil.log('Deleting all files...');

    return del('www/compressed/*');    
});

gulp.task('make-dir', function(){
    mkdirp('www/compressed', function (err) {
        if (err)
            gtuil.log(err);
        else 
            gutil.log('Creating compressed dir success');
    });
});

gulp.task('uglify-js', function(){
    gutil.log('Concatinating and Minifying All JS Files...');

    gulp.src('www/build/**/*.js')
    //.pipe(concat('minified.js'))
    .pipe(uglify_js())
    .pipe(gulp.dest('www/compressed/'));
});

gulp.task('minify-css', function(){
    gutil.log('Concatinating and Minifying All CSS Files...');

    gulp.src('www/build/**/*.css')
    //.pipe(concat('minified.css'))
    .pipe(clean_css())
    .pipe(gulp.dest('www/compressed/'));
});

gulp.task('minify-scss', function() {
    gutil.log('Concatinating and Minifying All SASS Files...');

    gulp.src('www/build/**/*.sass')
    .pipe(minify_scss())
    .pipe(gulp.dest('www/compressed/'));
});

gulp.task('image-min', function(){
    gutil.log('Minifying All Images...');

    gulp.src('www/img/*')
    .pipe(minify_img())
    .pipe(gulp.dest('www/compressed/min_img/'));
})