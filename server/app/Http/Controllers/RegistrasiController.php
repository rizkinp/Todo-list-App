<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;

class RegistrasiController extends Controller
{
    public function view()
    {
        $users = DB::table('users')
            ->join('prodi', 'users.prodi_id', '=', 'prodi.id')
            ->select('users.id', 'users.name', 'users.email', 'users.password', 'users.prodi_id', 'users.photo', 'prodi.name as prodi_name')
            ->orderByDesc('users.photo')
            ->get();

        $response = [];
        foreach ($users as $user) {
            $item = [
                'nama' => $user->name,
                'email' => $user->email,
                'password' => $user->password,
                'nama_prodi' => $user->prodi_name,
                'url' => $this->getImageUrl($user->photo)
            ];
            $response[] = $item;
        }

        return response()->json($response);
    }

    public function index(Request $r)
    {
        $nama = $r->input('nama');
        $users = DB::table('users')
            ->join('prodi', 'users.prodi_id', '=', 'prodi.id')
            ->select('users.id', 'users.name', 'users.email', 'users.password', 'users.prodi_id', 'users.photo', 'prodi.name as prodi_name')
            ->where('users.name', 'like', "%$nama%")
            ->get();

        $response = [];
        foreach ($users as $user) {
            $item = [
                'nama' => $user->name,
                'email' => $user->email,
                'password' => $user->password,
                'nama_prodi' => $user->prodi_name,
                'url' => $this->getImageUrl($user->photo)
            ];
            $response[] = $item;
        }

        return response()->json($response);
    }

    public function prodi()
    {
        $prodi = DB::table('prodi')->get();

        return response()->json($prodi);
    }

    public function insert(Request $r)
    {
        $mode = $r->input('mode');
        $nama = $r->input('nama');
        $email = $r->input('email');
        $password = $r->input('password');
        $namaProdi = $r->input('nama_prodi');
        $file = $r->input('file');
        $image = $r->input('photo');
        $idProdi = null;
        $res = null;

        if ($mode == 'insert') {
            $idProdi = DB::table('prodi')
                ->where('name', $namaProdi)
                ->value('id');
        }

        switch ($mode) {
            case 'insert':
                if ($image == null) {
                    $res = DB::table('users')->insert([
                        'name' => $nama,
                        'email' => $email,
                        'password' => $password,
                        'prodi_id' => $idProdi
                    ]);

                    if ($res == 1) {
                        $res = [
                            'kode' => '000',
                            'nama' => $nama,
                            'email' => $email,
                            'password' => $password,
                            'nama_prodi' => $namaProdi,
                            'url' => null
                        ];
                    } else {
                        $res = ['kode' => '111'];
                    }
                } else {
                    $res = DB::table('users')->insert([
                        'name' => $nama,
                        'email' => $email,
                        'password' => $password,
                        'prodi_id' => $idProdi,
                        'photo' => $file
                    ]);

                    if ($res == 1) {
                        if (Storage::disk('public')->put('images/' . $file, base64_decode($image))) {
                            $res = [
                                'kode' => '000',
                                'nama' => $nama,
                                'email' => $email,
                                'password' => $password,
                                'nama_prodi' => $namaProdi,
                                'url' => $this->getImageUrl($file)
                            ];
                        } else {
                            $res = ['kode' => '222'];
                        }
                    } else {
                        $res = ['kode' => '111'];
                    }
                }
                break;
        }

        return response()->json($res);
    }

    private function getImageUrl($fileName)
    {
        return Storage::disk('public')->url('images/' . $fileName);
    }
}
