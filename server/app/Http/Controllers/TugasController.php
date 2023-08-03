<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;

class TugasController extends Controller
{
    public function index()
    {
        $tugas = DB::table('tugas')->get();
        return response()->json($tugas);
    }

    public function show($id)
    {
        $tugas = DB::table('tugas')->where('id', $id)->first();
        return response()->json($tugas);
    }

    public function store(Request $request)
    {
        $tugas = [
            'topic' => $request->input('topic'),
            'user_id' => $request->input('user_id'),
            'mata_kuliah_id' => $request->input('mata_kuliah_id'),
            'description' => $request->input('description'),
            'due_date' => $request->input('due_date')
            
        ];

        $tugasId = DB::table('tugas')->insertGetId($tugas);
        $tugas['id'] = $tugasId;

        return response()->json($tugas, 201);
    }

    public function update(Request $request, $id)
    {
        $tugas = [
            'user_id' => $request->input('user_id'),
            'mata_kuliah_id' => $request->input('mata_kuliah_id'),
            'description' => $request->input('description'),
            'due_date' => $request->input('due_date'),
            'topic' => $request->input('topic')
        ];

        DB::table('tugas')->where('id', $id)->update($tugas);

        $tugas['id'] = $id;

        return response()->json($tugas);
    }

    public function destroy($id)
    {
        DB::table('tugas')->where('id', $id)->delete();

        return response()->json(null, 204);
    }

    public function getUserTasks($user_id)
    {
        $tugas = DB::table('tugas')->where('user_id', $user_id)->get();
        return response()->json($tugas);
    }
}

