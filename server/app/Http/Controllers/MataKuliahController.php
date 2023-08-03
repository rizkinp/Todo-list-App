<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class MataKuliahController extends Controller
{
    public function index()
    {
        $mataKuliah = DB::table('mata_kuliah')->get();
        return response()->json($mataKuliah);
    }

    public function show($id)
    {
        $mataKuliah = DB::table('mata_kuliah')->where('id', $id)->first();
        return response()->json($mataKuliah);
    }

    public function store(Request $request)
    {
        $mataKuliah = [
            'prodi_id' => $request->input('prodi_id'),
            'name' => $request->input('name')
        ];

        $mataKuliahId = DB::table('mata_kuliah')->insertGetId($mataKuliah);
        $mataKuliah['id'] = $mataKuliahId;

        return response()->json($mataKuliah, 201);
    }

    public function update(Request $request, $id)
    {
        $mataKuliah = [
            'prodi_id' => $request->input('prodi_id'),
            'name' => $request->input('name')
        ];

        DB::table('mata_kuliah')->where('id', $id)->update($mataKuliah);

        $mataKuliah['id'] = $id;

        return response()->json($mataKuliah);
    }

    public function destroy($id)
    {
        DB::table('mata_kuliah')->where('id', $id)->delete();

        return response()->json(null, 204);
    }

    public function getProdiMataKuliah($prodi_id)
    {
        $mataKuliah = DB::table('mata_kuliah')->where('prodi_id', $prodi_id)->get();
        return response()->json($mataKuliah);
    }
}
