<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class ProdiController extends Controller
{
    public function index()
    {
        $prodi = DB::table('prodi')->get();
        return response()->json($prodi);
    }

    public function show($id)
    {
        $prodi = DB::table('prodi')->where('id', $id)->first();
        return response()->json($prodi);
    }

    public function store(Request $request)
    {
        $prodi = [
            'name' => $request->input('name')
        ];

        $prodiId = DB::table('prodi')->insertGetId($prodi);
        $prodi['id'] = $prodiId;

        return response()->json($prodi, 201);
    }

    public function update(Request $request, $id)
    {
        $prodi = [
            'name' => $request->input('name')
        ];

        DB::table('prodi')->where('id', $id)->update($prodi);

        $prodi['id'] = $id;

        return response()->json($prodi);
    }

    public function destroy($id)
    {
        DB::table('prodi')->where('id', $id)->delete();

        return response()->json(null, 204);
    }
}
