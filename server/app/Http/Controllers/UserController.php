<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Storage;


class UserController extends Controller
{

    // public function getUser()
    // {
    //     // Mendapatkan ID pengguna yang sedang login
    //     $userId = Auth::id();

    //     // Mendapatkan data pengguna berdasarkan ID pengguna
    //     $user = User::find($userId);

    //     if (!$user) {
    //         return response()->json(['message' => 'User not found'], 404);
    //     }

    //     // Mengubah path gambar menjadi URL lengkap
    //     $photoUrl = Storage::url($user->photo);

    //     // Menambahkan URL gambar ke respons JSON
    //     $user->photo_url = $photoUrl;

    //     return response()->json(['user' => $user]);
    // }

    public function index()
    {
        $users = DB::table('users')->get();
        return response()->json($users);
    }

    public function show($id)
    {
        $user = DB::table('users')->where('id', $id)->first();
        return response()->json($user);
    }

    public function store(Request $request)
{
    $name = $request->input('name');
    $email = $request->input('email');
    $password = $request->input('password');
    $prodiId = $request->input('prodi_id');
    $photoBase64 = $request->input('photo');

    if (empty($name) || empty($email) || empty($password) || empty($photoBase64)) {
        return response()->json(['message' => 'Incomplete data'], 400);
    }

    $photoData = base64_decode($photoBase64);
    $photoPath = 'storage/images/' . uniqid() . '.jpg';

    file_put_contents($photoPath, $photoData);

    $photoUrl = 'http://192.168.60.64/project_uas_todo/public/' . $photoPath; // Ganti dengan URL server atau URL penyimpanan file Anda

    $userId = DB::table('users')->insertGetId([
        'name' => $name,
        'email' => $email,
        'password' => $password,
        'prodi_id' => $prodiId,
        'photo' => $photoUrl,
    ]);

    $user = DB::table('users')->where('id', $userId)->first();

    return response()->json($user, 201);
}


    public function update1(Request $request, $id)
    {
        $user = [
            'name' => $request->input('name'),
            'email' => $request->input('email'),
            'password' => $request->input('password'),
            'prodi_id' => $request->input('prodi_id'),
            'photo' => $request->input('photo')
        ];

        DB::table('users')->where('id', $id)->update($user);

        $user['id'] = $id;

        return response()->json($user);
    }

    public function destroy($id)
    {
        DB::table('users')->where('id', $id)->delete();

        return response()->json(null, 204);
    }

    public function update(Request $request, $id)
{
    $user = DB::table('users')->where('id', $id)->first();

    $name = $user->name;
    $email = $request->input('email', $user->email); // Menggunakan email baru jika ada, jika tidak, tetap menggunakan email yang ada
    $password = $request->input('password', $user->password); // Menggunakan password baru jika ada, jika tidak, tetap menggunakan password yang ada
    $prodiId = $user->prodi_id;
    $photoBase64 = $user->photo;

    if (empty($name) || empty($email) || empty($password) || empty($photoBase64)) {
        return response()->json(['message' => 'Incomplete data'], 400);
    }

    // Perbarui data pengguna di database
    DB::table('users')
        ->where('id', $id)
        ->update([
            'name' => $name,
            'email' => $email,
            'password' => $password,
            'prodi_id' => $prodiId,
            'photo' => $photoBase64,
        ]);

    $updatedUser = DB::table('users')->where('id', $id)->first();

    return response()->json($updatedUser);
}

}

