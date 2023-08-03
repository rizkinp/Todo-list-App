<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\MataKuliahController;
use App\Http\Controllers\ProdiController;
use App\Http\Controllers\UserController;
use App\Http\Controllers\TugasController;
use App\Http\Controllers\HistoryController;
use App\Http\Controllers\RegistrasiController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});

//Route untuk prodi

    Route::get('/prodi', [ProdiController::class, 'index']);
    Route::get('/prodi/{id}', [ProdiController::class, 'show']);
    Route::post('/prodi', [ProdiController::class, 'store']);
    Route::put('/prodi/{id}', [ProdiController::class, 'update']);
    Route::delete('/prodi/{id}', [ProdiController::class, 'destroy']);


    //Route untuk matkul

    Route::get('/mata-kuliah', [MataKuliahController::class, 'index']);
    Route::get('/mata-kuliah/{id}', [MataKuliahController::class, 'show']);
    Route::post('/mata-kuliah', [MataKuliahController::class, 'store']);
    Route::put('/mata-kuliah/{id}', [MataKuliahController::class, 'update']);
    Route::delete('/mata-kuliah/{id}', [MataKuliahController::class, 'destroy']);
    Route::get('/mata-kuliah/prodi/{prodi_id}', [MataKuliahController::class, 'getProdiMataKuliah']);

    //Route untuk user

    Route::get('/users', [UserController::class, 'index']);
    Route::post('/users', [UserController::class, 'store']);
    Route::get('/users/{id}', [UserController::class, 'show']);
    Route::put('/users/{id}', [UserController::class, 'update']);
    Route::delete('/users/{id}', [UserController::class, 'destroy']);
    Route::get('/users/{userId}', [UserController::class, 'getUser']);

    //route untuk tugas

    Route::get('/tugas', [TugasController::class, 'index']);
    Route::get('/tugas/{id}', [TugasController::class, 'show']);
    Route::post('/tugas', [TugasController::class, 'store']);
    Route::put('/tugas/{id}', [TugasController::class, 'update']);
    Route::delete('/tugas/{id}', [TugasController::class, 'destroy']);
    Route::get('tugas/user/{user_id}', [TugasController::class,'getUserTasks']);

    //route untuk history
    Route::get('/history', [HistoryController::class, 'index']);
    Route::get('/history/{id}', [HistoryController::class, 'show']);
    Route::post('/history', [HistoryController::class, 'store']);
    Route::put('/history/{id}', [HistoryController::class, 'update']);
    Route::delete('/history/{id}', [HistoryController::class, 'destroy']);
    Route::get('/history/user/{userId}', [HistoryController::class, 'showByUserId']);



    //route untuk register
    Route::get('/register', [RegistrasiController::class, 'view']);
Route::post('/register', [RegistrasiController::class, 'index']);
Route::get('/register/prodi', [RegistrasiController::class, 'prodi']);
Route::post('/register', [RegistrasiController::class, 'insert']);
Route::post('/uploadFoto', [RegistrasiController::class, 'uploadFoto']);



