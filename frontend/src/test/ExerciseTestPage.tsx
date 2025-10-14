// ExerciseTestPage.tsx (GlobalStyle 기반, any 제거)
// - 필요한 import 경로는 프로젝트에 맞게 조정하세요.

import React, { JSX, useMemo } from 'react';
import { ExerciseCreateRequest, ExerciseIsHiddenBatchUpdateRequest, ExerciseUpdateRequest, MetricRequirement } from '../types/domain/exercise';
import useExercise from '../hooks/useExercise';

// MetricRequirement 추론 타입 (도메인 타입에서 가져오도록 구성)
const METRIC_OPTIONS: MetricRequirement[] = [MetricRequirement.FORBIDDEN, MetricRequirement.OPTIONAL, MetricRequirement.REQUIRED] as const;

// 유틸 타입 추론
type MetricShape = ExerciseCreateRequest['metricRequirement'];
type InstructionCreate = ExerciseCreateRequest['instructions'][number];
type InstructionUpdate = ExerciseUpdateRequest['instructions'][number];

export default function ExerciseTestPage(): JSX.Element {
  const {
    // paging
    exerciseListPage,

    // list
    exerciseList,
    fetchExerciseList,
    nextExercisePage,
    clearExercisePage,

    // detail
    selectedExercise,
    loadExerciseDetailInfo,
    initSelectedExercise,

    // create
    exerciseCreateForm,
    setExerciseCreateForm,
    buildExerciseCreateForm,
    submitExerciseCreate,

    // update
    exerciseUpdateForm,
    setExerciseUpdateForm,
    buildUpdateExerciseForm,
    submitExerciseUpdate,

    // batch activation
    selectedIdList,
    setSelectedIdList,
    updateExerciseState,
  } = useExercise();

  // ------- helpers -------
  const toggleBatch = (id: number, mode: 'activate' | 'deactivate') => {
    setSelectedIdList((prev): ExerciseIsHiddenBatchUpdateRequest => {
        const act = new Set<number>(prev.activate.exerciseIds);
        const deact = new Set<number>(prev.deactivate.exerciseIds);
        if (mode === 'activate') {
          if (act.has(id)) {
            act.delete(id);
          } else {
            act.add(id);
          }
        if (deact.has(id)) {
          deact.delete(id);
        }
        } else {
            if (deact.has(id)) {
              deact.delete(id);
            } else {
              deact.add(id);
            }
          if (act.has(id)) {
            act.delete(id);
          }
        }
      return {
        activate: { exerciseIds: Array.from(act) },
        deactivate: { exerciseIds: Array.from(deact) },
      };
    });
  };

  const isMarked = (id: number) => ({
    activate: selectedIdList.activate.exerciseIds.includes(id),
    deactivate: selectedIdList.deactivate.exerciseIds.includes(id),
  });

  const canCreate = useMemo(() => Boolean(exerciseCreateForm), [exerciseCreateForm]);
  const canUpdate = useMemo(() => Boolean(exerciseUpdateForm), [exerciseUpdateForm]);

  // ------- UI (GlobalStyle 클래스 사용) -------
  return (
    <div className="container" style={{ paddingTop: '24px', paddingBottom: '48px' }}>
      <header style={{ display: 'flex', gap: '12px', alignItems: 'center', justifyContent: 'space-between', marginBottom: '24px' }}>
        <h1 className="fs-xl" style={{ margin: 0 }}>Exercise Test Page</h1>
        <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
          <button className="btn-ghost" onClick={() => fetchExerciseList()}>Reload</button>
          <button className="btn-ghost" onClick={() => nextExercisePage()}>Next Page</button>
          <button className="btn-ghost" onClick={() => clearExercisePage()}>Reset Page</button>
          <div className="fs-sm" style={{ color: 'var(--text-3)' }}>
            page={exerciseListPage.page} size={exerciseListPage.size} sort={exerciseListPage.sort}
          </div>
        </div>
      </header>

      {/* List */}
      <section className="card" style={{ padding: '16px', marginBottom: '24px' }}>
        <h2 className="fs-lg" style={{ marginTop: 0 }}>Exercises</h2>
        <div style={{ overflowX: 'auto', border: `1px solid var(--border-1)`, borderRadius: 'var(--radius-md)' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 'var(--fs-sm)' }}>
            <thead style={{ background: 'var(--bg-elev-2)' }}>
              <tr>
                {['ID', 'Name', 'Category', 'Hidden', 'Mark', 'Action'].map(h => (
                  <th key={h} style={{ textAlign: 'left', padding: '12px', borderBottom: `1px solid var(--border-1)` }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {exerciseList.map(e => {
                const mark = isMarked(e.id);
                return (
                  <tr key={e.id} style={{ borderTop: `1px solid var(--border-1)` }}>
                    <td style={{ padding: '10px 12px' }}>{e.id}</td>
                    <td style={{ padding: '10px 12px' }}>{e.name}</td>
                    <td style={{ padding: '10px 12px' }}>{e.category}</td>
                    <td style={{ padding: '10px 12px' }}>{String(e.isHidden)}</td>
                    <td style={{ padding: '10px 12px' }}>
                      <label style={{ marginRight: 12, display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                        <input type="checkbox" checked={mark.activate} onChange={() => toggleBatch(e.id, 'activate')} />
                        <span className="fs-sm">Activate</span>
                      </label>
                      <label style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
                        <input type="checkbox" checked={mark.deactivate} onChange={() => toggleBatch(e.id, 'deactivate')} />
                        <span className="fs-sm">Deactivate</span>
                      </label>
                    </td>
                    <td style={{ padding: '10px 12px' }}>
                      <button className="btn-primary" onClick={() => loadExerciseDetailInfo(e.id)}>Load Detail</button>
                    </td>
                  </tr>
                );
              })}
              {exerciseList.length === 0 && (
                <tr>
                  <td colSpan={6} style={{ padding: '14px', textAlign: 'center', color: 'var(--text-3)' }}>No data</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 12 }}>
          <button className="btn-primary" onClick={updateExerciseState}>Apply Activation Changes</button>
          <div className="fs-sm" style={{ color: 'var(--text-3)' }}>
            activate=[{selectedIdList.activate.exerciseIds.join(', ')}] | deactivate=[{selectedIdList.deactivate.exerciseIds.join(', ')}]
          </div>
        </div>
      </section>

      {/* Detail */}
      <section className="card" style={{ padding: '16px' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
          <h2 className="fs-lg" style={{ margin: 0 }}>Detail</h2>
          <div style={{ display: 'flex', gap: 8 }}>
            <button className="btn-ghost" onClick={initSelectedExercise}>Clear</button>
            {selectedExercise && (
              <>
                <button className="btn-ghost" onClick={() => buildExerciseCreateForm(selectedExercise)}>Build Create Form</button>
                <button className="btn-ghost" onClick={() => buildUpdateExerciseForm(selectedExercise)}>Build Update Form</button>
              </>
            )}
          </div>
        </div>

        {selectedExercise ? (
          <div style={{ display: 'grid', gap: 16, gridTemplateColumns: '1fr', alignItems: 'start' }}>
            <div className="card" style={{ padding: 12 }}>
              <pre style={{ margin: 0, whiteSpace: 'pre-wrap', wordBreak: 'break-word', fontSize: '12px' }}>
                {JSON.stringify(selectedExercise, null, 2)}
              </pre>
            </div>

            <div style={{ display: 'grid', gap: 16, gridTemplateColumns: '1fr', alignItems: 'start' }}>
              {/* Create form */}
              <div className="card" style={{ padding: 12 }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                  <h3 style={{ margin: 0 }}>Create Form</h3>
                  <button className="btn-primary" onClick={submitExerciseCreate} disabled={!canCreate}>Create</button>
                </div>
                {exerciseCreateForm ? (
                  <FormEditorCreate form={exerciseCreateForm} onChange={setExerciseCreateForm} />
                ) : ( 
                  <p className="fs-sm" style={{ color: 'var(--text-3)' }}>"Build Create Form" 버튼으로 초기화하세요.</p>
                )}
              </div>

              {/* Update form */}
              <div className="card" style={{ padding: 12 }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
                  <h3 style={{ margin: 0 }}>Update Form</h3>
                  <button className="btn-primary" onClick={submitExerciseUpdate} disabled={!canUpdate || !selectedExercise}>Update</button>
                </div>
                {exerciseUpdateForm ? (
                  <FormEditorUpdate form={exerciseUpdateForm} onChange={setExerciseUpdateForm} />
                ) : (
                  <p className="fs-sm" style={{ color: 'var(--text-3)' }}>"Build Update Form" 버튼으로 초기화하세요.</p>
                )}
              </div>
            </div>
          </div>
        ) : (
          <p className="fs-sm" style={{ color: 'var(--text-3)' }}>행을 선택한 뒤 "Load Detail"을 클릭하세요.</p>
        )}
      </section>
    </div>
  );
}

// ------- Create Form Editor -------
function FormEditorCreate({
  form,
  onChange,
}: {
  form: ExerciseCreateRequest;
  onChange: (f: ExerciseCreateRequest) => void;
}): JSX.Element {
  const update = (patch: Partial<ExerciseCreateRequest>) => onChange({ ...form, ...patch });

  return (
    <div style={{ display: 'grid', gap: 12 }}>
      <div style={{ display: 'grid', gap: 8, gridTemplateColumns: '1fr 1fr' }}>
        <LabeledInput label="Name" value={form.name} onChange={v => update({ name: v })} />
        <LabeledInput label="Category" value={form.category} onChange={v => update({ category: v })} />
      </div>
      <LabeledTextarea label="Description" value={form.description ?? ''} onChange={v => update({ description: v })} />
      <label style={{ display: 'inline-flex', alignItems: 'center', gap: 8 }}>
        <input type="checkbox" checked={Boolean(form.isHidden)} onChange={e => update({ isHidden: e.target.checked })} />
        <span className="fs-sm">isHidden</span>
      </label>

      <MetricEditor metric={form.metricRequirement} onChange={m => update({ metricRequirement: m })} />

      <InstructionListEditorCreate items={form.instructions ?? []} onChange={items => update({ instructions: items })} />
    </div>
  );
}

// ------- Update Form Editor -------
function FormEditorUpdate({
  form,
  onChange,
}: {
  form: ExerciseUpdateRequest;
  onChange: (f: ExerciseUpdateRequest) => void;
}): JSX.Element {
  const update = (patch: Partial<ExerciseUpdateRequest>) => onChange({ ...form, ...patch });

  return (
    <div style={{ display: 'grid', gap: 12 }}>
      <div style={{ display: 'grid', gap: 8, gridTemplateColumns: '1fr 1fr' }}>
        <LabeledInput label="Name" value={form.name} onChange={v => update({ name: v })} />
        <LabeledInput label="Category" value={form.category} onChange={v => update({ category: v })} />
      </div>
      <LabeledTextarea label="Description" value={form.description ?? ''} onChange={v => update({ description: v })} />
      <label style={{ display: 'inline-flex', alignItems: 'center', gap: 8 }}>
        <input type="checkbox" checked={Boolean(form.isHidden)} onChange={e => update({ isHidden: e.target.checked })} />
        <span className="fs-sm">isHidden</span>
      </label>

      <MetricEditor metric={form.metricRequirement} onChange={m => update({ metricRequirement: m })} />

      <InstructionListEditorUpdate items={form.instructions ?? []} onChange={items => update({ instructions: items })} />
    </div>
  );
}

// ------- Subcomponents (GlobalStyle 폼 규칙 준수) -------
function LabeledInput({ label, value, onChange }: { label: string; value: string; onChange: (v: string) => void }): JSX.Element {
  return (
    <label className="fs-sm">
      <div style={{ marginBottom: 6, color: 'var(--text-2)' }}>{label}</div>
      <input value={value ?? ''} onChange={e => onChange(e.target.value)} />
    </label>
  );
}

function LabeledTextarea({ label, value, onChange }: { label: string; value: string; onChange: (v: string) => void }): JSX.Element {
  return (
    <label className="fs-sm" style={{ display: 'block' }}>
      <div style={{ marginBottom: 6, color: 'var(--text-2)' }}>{label}</div>
      <textarea className="fs-sm" value={value ?? ''} onChange={e => onChange(e.target.value)} />
    </label>
  );
}

function MetricEditor({ metric, onChange }: { metric: MetricShape; onChange: (m: MetricShape) => void }): JSX.Element {
  const update = (patch: Partial<MetricShape>) => onChange({ ...metric, ...patch });

  return (
    <div style={{ display: 'grid', gap: 8, gridTemplateColumns: 'repeat(4, minmax(0,1fr))' }}>
      <SelectField label="weightKgStatus" value={metric?.weightKgStatus ?? MetricRequirement.FORBIDDEN} onChange={v => update({ weightKgStatus: v })} />
      <SelectField label="repsStatus" value={metric?.repsStatus ?? MetricRequirement.FORBIDDEN} onChange={v => update({ repsStatus: v })} />
      <SelectField label="distanceMeterStatus" value={metric?.distanceMeterStatus ?? MetricRequirement.FORBIDDEN} onChange={v => update({ distanceMeterStatus: v })} />
      <SelectField label="durationSecondStatus" value={metric?.durationSecondStatus ?? MetricRequirement.FORBIDDEN} onChange={v => update({ durationSecondStatus: v })} />
    </div>
  );
}

function SelectField({ label, value, onChange }: { label: string; value: MetricRequirement; onChange: (v: MetricRequirement) => void }): JSX.Element {
  return (
    <label className="fs-sm">
      <div style={{ marginBottom: 6, color: 'var(--text-2)' }}>{label}</div>
      <select value={value} onChange={e => onChange(e.target.value as MetricRequirement)}>
        {METRIC_OPTIONS.map(opt => (
          <option key={opt} value={opt}>{opt}</option>
        ))}
      </select>
    </label>
  );
}

function InstructionListEditorCreate({ items, onChange }: { items: InstructionCreate[]; onChange: (next: InstructionCreate[]) => void }): JSX.Element {
  const add = () => onChange([...(items || []), { stepOrder: (items?.length || 0) + 1, description: '' }]);
  const remove = (idx: number) => onChange(items.filter((_, i) => i !== idx));
  const patch = (idx: number, patchItem: Partial<InstructionCreate>) => onChange(items.map((it, i) => (i === idx ? { ...it, ...patchItem } : it)));

  return (
    <div style={{ display: 'grid', gap: 8 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <h4 style={{ margin: 0 }}>Instructions</h4>
        <button className="btn-ghost" onClick={add}>Add</button>
      </div>
      <div style={{ display: 'grid', gap: 8 }}>
        {items && items.length > 0 ? (
          items.map((it, idx) => (
            <div key={idx} style={{ display: 'grid', gap: 8, gridTemplateColumns: '1fr 3fr auto', alignItems: 'end' }}>
              <LabeledInput label="stepOrder" value={String(it.stepOrder ?? '')} onChange={v => patch(idx, { stepOrder: v ? Number(v) : undefined })} />
              <LabeledInput label="description" value={it.description ?? ''} onChange={v => patch(idx, { description: v })} />
              <button className="btn-ghost" onClick={() => remove(idx)}>Del</button>
            </div>
          ))
        ) : (
          <p className="fs-sm" style={{ color: 'var(--text-3)' }}>No instructions. Add one.</p>
        )}
      </div>
    </div>
  );
}

function InstructionListEditorUpdate({ items, onChange }: { items: InstructionUpdate[]; onChange: (next: InstructionUpdate[]) => void }): JSX.Element {
  const add = () => onChange([...(items || []), { id: undefined as unknown as number, stepOrder: (items?.length || 0) + 1, description: '' }]);
  const remove = (idx: number) => onChange(items.filter((_, i) => i !== idx));
  const patch = (idx: number, patchItem: Partial<InstructionUpdate>) => onChange(items.map((it, i) => (i === idx ? { ...it, ...patchItem } : it)));

  return (
    <div style={{ display: 'grid', gap: 8 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <h4 style={{ margin: 0 }}>Instructions</h4>
        <button className="btn-ghost" onClick={add}>Add</button>
      </div>
      <div style={{ display: 'grid', gap: 8 }}>
        {items && items.length > 0 ? (
          items.map((it, idx) => (
            <div key={idx} style={{ display: 'grid', gap: 8, gridTemplateColumns: '1fr 1fr 3fr auto', alignItems: 'end' }}>
              <LabeledInput label="id" value={it.id ? String(it.id) : ''} onChange={v => patch(idx, { id: v ? Number(v) : (undefined as unknown as number) })} />
              <LabeledInput label="stepOrder" value={String(it.stepOrder ?? '')} onChange={v => patch(idx, { stepOrder: v ? Number(v) : undefined })} />
              <LabeledInput label="description" value={it.description ?? ''} onChange={v => patch(idx, { description: v })} />
              <button className="btn-ghost" onClick={() => remove(idx)}>Del</button>
            </div>
          ))
        ) : (
          <p className="fs-sm" style={{ color: 'var(--text-3)' }}>No instructions. Add one.</p>
        )}
      </div>
    </div>
  );
}
